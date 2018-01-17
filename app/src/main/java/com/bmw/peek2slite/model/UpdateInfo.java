package com.bmw.peek2slite.model;

public class UpdateInfo
{
        private String version;
        private String description;
        private String url;
        private String apkName;
        private String baseName;

        public String getApkName() {
                return apkName;
        }

        public void setApkName(String apkName) {
                this.apkName = apkName;
        }

        public String getVersion()
        {
                return version;
        }
        public void setVersion(String version)
        {
                this.version = version;
        }
        public String getDescription()
        {
                return description;
        }
        public void setDescription(String description)
        {
                this.description = description;
        }
        public String getUrl()
        {
                return url;
        }
        public void setUrl(String url)
        {
                this.url = url;
        }

        public String getBaseName() {
                return baseName;
        }

        public void setBaseName(String baseName) {
                this.baseName = baseName;
        }

        @Override
        public String toString() {
                return "UpdateInfo{" +
                        "version='" + version + '\'' +
                        ", description='" + description + '\'' +
                        ", url='" + url + '\'' +
                        ", apkName='" + apkName + '\'' +
                        ", baseName='" + baseName + '\'' +
                        '}';
        }
}
