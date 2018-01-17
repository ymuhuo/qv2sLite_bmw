package com.bmw.peek2slite.utils;

import com.bmw.peek2slite.model.All_id_Info;
import com.bmw.peek2slite.model.Login_info;
import com.bmw.peek2slite.utils.singleThreadUtil.FinalizableDelegatedExecutorService;
import com.bmw.peek2slite.utils.singleThreadUtil.RunnablePriority;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2016/9/19.
 */
public class SocketUtilNew {

    //    private PreviewImpl preview;
    private Socket socket;
    private OutputStream socketWriter;
    private InputStream socketReader;
    private boolean isFinish;
    private Login_info loginInfo;
    public ExecutorService singleThreadExecutor = new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>()));
    private long current_time;
    private static SocketUtilNew instance;
    private String lastActionName;
    ExecutorService caCheThreadPool;

    private ArrayList<Integer> backList;
    private byte[] backByte;
    private Integer backArgLength;
    private boolean isResetSocket;


    public static SocketUtilNew getInstance() {
        if (instance == null) {
            synchronized (All_id_Info.class) {
                if (instance == null)
                    instance = new SocketUtilNew();
            }
        }
        return instance;
    }

    public boolean isSocketNull() {
        if (socket == null)
            return true;
        else
            return false;
    }

    private SocketUtilNew() {
        loginInfo = Login_info.getInstance();
        caCheThreadPool = Executors.newCachedThreadPool();
        initSocket();
        read();
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //初始化socket
    public void initSocket() {

        if (caCheThreadPool == null || caCheThreadPool.isShutdown()) {
            caCheThreadPool = Executors.newCachedThreadPool();
        }

        if (singleThreadExecutor == null || singleThreadExecutor.isShutdown()) {
            singleThreadExecutor = new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>()));
        }
        isFinish = true;
    }

    public void resetSocket() {
        caCheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                    socket = null;
                    socket = new Socket(loginInfo.getSocket_ip(), loginInfo.getSocket_port());
                    log("数据:socket连接 connect: ", String.valueOf(socket.isBound()));
                    socket.setSoTimeout(5000);
                    socketWriter = socket.getOutputStream();
                    socketReader = socket.getInputStream();
                    log("数据:socket连接:  连接成功!", "");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (socket == null) {
                    resetSocket();
                }
            }
        });
    }

    //发送命令（控制方向、速度的命令）
    public void sendcmd(byte[] commands, String action_name) {
        if (socket == null) {
            error("数据:发送", action_name + "命令失败：sendcmd: socket is null");
            return;
        }
        if (socketWriter == null)
            try {
                socketWriter = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            socketWriter.write(commands);
            socketWriter.flush();
//            log("数据:sendcmd: 已经发送 " , action_name + " 命令！");
        } catch (IOException e) {
            if (socket != null && !socket.isClosed()) {
                error("数据:socketService is already closed!  ", "");
                socket = null;
                initSocket();
            }
            e.printStackTrace();
        }
    }

    public void release() {
        isFinish = false;
        if (socket != null) {
            try {
                singleThreadExecutor.shutdownNow();
                caCheThreadPool.shutdownNow();
                socket.shutdownInput();
                socket.shutdownOutput();
                socketReader.close();
                socketWriter.close();
                if (socket != null)
                    socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log("数据:socket断开连接: Socket已经释放内存！", "");
    }

    public void getReader(final byte[] commands,
                          final int priority,
                          final String action_name, final int which) {
        if (socket == null) {
            LogUtil.error("数据:发送命令失败：sendcmd: socket is null", action_name);
            if (!isResetSocket) {
                resetSocket();
                isResetSocket = true;

            }
            return;
        }


        isResetSocket = false;
        if (socketReader == null)
            try {
                socketReader = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }


        if (socket != null) {
            log("数据:单线程，命令放入队列 ", action_name);
            singleThreadExecutor.execute(new RunnablePriority(priority) {
                @Override
                public void run() {
                    log("数据:单线程运行命令:", action_name + "线程号：" + Thread.currentThread().getId());
                    while ((action_name.contains("停止")) && System.currentTimeMillis() - current_time <= 150) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!action_name.contains("停止")) {
                        while (System.currentTimeMillis() - current_time <= 100) {
                            sleep(5);
                        }
                    }


                    sendcmd(commands, action_name);
//                    sleep(1000*10);

                    if (action_name.contains("停止")) {
                        sleep(50);
                    }

                    current_time = System.currentTimeMillis();
                    lastActionName = action_name;
                }
            });
        }
    }


    public void read() {
        backList = new ArrayList<>();
        backByte = new byte[1];
        caCheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (isFinish) {
                    if (socket != null && socketReader != null) {
                        try {
                            socketReader.read(backByte);
                            if (backList.size() == 0) {
                                if ((backByte[0] & 0xff) == 0x01) {
                                    backList.add(backByte[0] & 0xff);
                                    LogUtil.log("socket读取：读取帧头成功", "");
                                }
                            } else {
                                backList.add(backByte[0] & 0xff);
                            }
                            if (backList.size() == 3) {
                                backArgLength = backList.get(2);
                                LogUtil.log("socket读取：确定帧参数长度：", String.valueOf(backArgLength));
                            }
                            if (backList.size() > 3) {
                                if (backList.size() == 3 + backArgLength) {
                                    LogUtil.log("socket读取：帧读取完成：", String.valueOf(backList.size()));
                                    int sum = 0;
                                    for (int i = 0; i < backList.size() - 1; i++) {
                                        sum += backList.get(i);
                                    }
                                    sum = sum % 0x100;
                                    if (sum == backList.get(backList.size() - 1)) {
                                        byte[] backResult = new byte[backList.size()];
                                        for (int i = 0; i < backList.size(); i++) {
                                            backResult[i] = (byte) (int) backList.get(i);
                                        }
                                        if (listener != null)
                                            listener.result(backResult);
                                        LogUtil.log("socket读取：帧校验成功：", "");
                                    }

                                    backList.clear();
                                    backArgLength = 0;
                                }
                            }

                        } catch (SocketTimeoutException e) {
                            if (!isResetSocket) {
                                resetSocket();

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
    }


    public interface OnDataReaderListener {
        void result(byte[] bytes);
    }

    public OnDataReaderListener listener;


    public void setOnDataReaderListener(OnDataReaderListener listener) {
        this.listener = listener;
    }

    private void log(String key, String msg) {
        LogUtil.log(key, msg);
    }

    private void error(String key, String msg) {
        LogUtil.error(key, msg);
    }

}

