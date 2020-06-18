package net.onlyid.sdk;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

public class OnlyID {
    static OAuthListener listener;

    public static void oauth(Context context, OAuthConfig config, OAuthListener listener) {
        OnlyID.listener = listener;

        Intent intent = new Intent(context, OAuthActivity.class);
        intent.putExtra("config", config);
        context.startActivity(intent);
    }

    public static class OAuthConfig implements Serializable {
        public String clientId;
        public String view;
        public String theme;
        public String state;

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
