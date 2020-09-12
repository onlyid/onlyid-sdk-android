package net.onlyid.sdk;

import java.io.Serializable;

public class OAuthConfig implements Serializable {
    public String clientId; // 你的应用id
    public String view; // 显示界面，设置为zoomed 放大显示，否则默认正常显示
    public String theme; // 主题样式，设置为dark 夜间主题，否则默认日间主题
    public String state; // oauth安全相关，不懂可以忽略

    public OAuthConfig(String clientId) {
        this(clientId, null, null, null);
    }

    public OAuthConfig(String clientId, String view, String theme, String state) {
        this.clientId = clientId;
        this.view = view;
        this.theme = theme;
        this.state = state;
    }
}
