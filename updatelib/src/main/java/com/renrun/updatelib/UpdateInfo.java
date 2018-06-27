package com.renrun.updatelib;

import java.io.Serializable;

public class UpdateInfo implements Serializable {

    private static final long serialVersionUID = 7247714666080613254L;

    /**
     * r : 1
     * msg :
     * item : {"version":"1","versionName":"1.0.5","updateMsg":"修复若干问题，改善软件稳定性","downloadUrl":"http://www.niurenjie.com//index.php/Index/AppUpdate/upkey/eccbc87e4b5ce2fe28308fd9f2a7baf3","publishDate":"2017-09-21","force":"","filesize":"7697009"}
     */

    private int r;
    private String msg;
    private ItemBean item;

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ItemBean getItem() {
        return item;
    }

    public void setItem(ItemBean item) {
        this.item = item;
    }

    public static class ItemBean implements Serializable {

        private static final long serialVersionUID = 7247714666080613266L;
        /**
         * version : 1
         * versionName : 1.0.5
         * updateMsg : 修复若干问题，改善软件稳定性
         * downloadUrl : http://www.niurenjie.com//index.php/Index/AppUpdate/upkey/eccbc87e4b5ce2fe28308fd9f2a7baf3
         * publishDate : 2017-09-21
         * force :
         * filesize : 7697009
         */

        private String version;
        private String versionName;
        private String updateMsg;
        private String downloadUrl;
        private String publishDate;
        private String force;
        private String filesize;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getUpdateMsg() {
            return updateMsg;
        }

        public void setUpdateMsg(String updateMsg) {
            this.updateMsg = updateMsg;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getPublishDate() {
            return publishDate;
        }

        public void setPublishDate(String publishDate) {
            this.publishDate = publishDate;
        }

        public String getForce() {
            return force;
        }

        public void setForce(String force) {
            this.force = force;
        }

        public String getFilesize() {
            return filesize;
        }

        public void setFilesize(String filesize) {
            this.filesize = filesize;
        }
    }
}
