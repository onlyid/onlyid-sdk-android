package net.onlyid.sdk;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

/**
 * SDK主入口
 */
public class OnlyID {
    static OAuthListener listener;

    /**
     * 发起oauth请求，打开授权页
     */
    public static void oauth(Context context, OAuthConfig config, OAuthListener listener) {
        OnlyID.listener = listener;

        Intent intent = new Intent(context, OAuthActivity.class);
        intent.putExtra("config", config);
        context.startActivity(intent);
    }

    public static class OAuthConfig implements Serializable {
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

    public interface OAuthListener {
        void onComplete(String code, String state);
        void onError(ErrCode errCode);
        void onCancel();
    }

    public enum ErrCode {
        NETWORK_ERR("网络错误，请检查");

        public String msg;
        ErrCode(String msg) {
            this.msg = msg;
        }
    }
}
