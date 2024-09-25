package com.socialmediasaver.status.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InstaUserSearchRaoidModel {
    @SerializedName("user")
    @Expose
    private User user;

    public User getUser() {
        return user;
    }


    public class User {
        private String full_name;

        public String getFull_name() {
            return full_name;
        }

        public String getUsername() {
            return username;
        }

        public String getProfile_pic_url() {
            return profile_pic_url;
        }

        public Integer getFollower_count() {
            return follower_count;
        }

        public Integer getFollowing_count() {
            return following_count;
        }

        public HdProfilePicUrlInfo getHd_profile_pic_url_info() {
            return hd_profile_pic_url_info;
        }

        public List<HdProfilePicVersion> getHd_profile_pic_versions() {
            return hd_profile_pic_versions;
        }

        private String username;
        private String profile_pic_url;
        private Integer follower_count, following_count;
        private HdProfilePicUrlInfo hd_profile_pic_url_info;
        private List<HdProfilePicVersion> hd_profile_pic_versions = null;
    }

    public class HdProfilePicVersion {

        @SerializedName("width")
        @Expose
        private Integer width;
        @SerializedName("height")
        @Expose
        private Integer height;
        @SerializedName("url")
        @Expose
        private String url;

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }


    public class HdProfilePicUrlInfo {

        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("width")
        @Expose
        private Integer width;
        @SerializedName("height")
        @Expose
        private Integer height;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

    }
}

