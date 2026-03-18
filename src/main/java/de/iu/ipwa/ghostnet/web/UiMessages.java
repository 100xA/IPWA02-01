package de.iu.ipwa.ghostnet.web;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class UiMessages {

    private static final String BUNDLE_VAR = "msg";

    private UiMessages() {
    }

    public static String text(String key) {
        if (key == null) {
            return null;
        }

        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            return key;
        }

        try {
            ResourceBundle bundle = context
                .getApplication()
                .getResourceBundle(context, BUNDLE_VAR);
            if (bundle != null && bundle.containsKey(key)) {
                return bundle.getString(key);
            }
        } catch (MissingResourceException ignored) {
            // fall through
        }

        return key;
    }

    public static boolean hasKey(String key) {
        if (key == null) {
            return false;
        }

        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            return false;
        }

        try {
            ResourceBundle bundle = context
                .getApplication()
                .getResourceBundle(context, BUNDLE_VAR);
            return bundle != null && bundle.containsKey(key);
        } catch (MissingResourceException ignored) {
            return false;
        }
    }

    public static FacesMessage info(String detailKey) {
        return new FacesMessage(
            FacesMessage.SEVERITY_INFO,
            text("message.success"),
            text(detailKey)
        );
    }

    public static FacesMessage notice(String detailKey) {
        return new FacesMessage(
            FacesMessage.SEVERITY_INFO,
            text("message.info"),
            text(detailKey)
        );
    }

    public static FacesMessage error(String detailKey) {
        return new FacesMessage(
            FacesMessage.SEVERITY_ERROR,
            text("message.error"),
            text(detailKey)
        );
    }

    public static FacesMessage errorDetail(String detail) {
        return new FacesMessage(
            FacesMessage.SEVERITY_ERROR,
            text("message.error"),
            detail
        );
    }

    public static String resolveDetailMessage(Throwable error, String fallbackKey) {
        Throwable cursor = error;
        while (cursor != null) {
            String msg = cursor.getMessage();
            if (msg != null) {
                String normalized = msg.toLowerCase(Locale.ROOT);
                if (normalized.contains("access denied for user")) {
                    return text("error.db.accessDenied");
                }
                if (normalized.contains("communications link failure")) {
                    return text("error.db.unreachable");
                }
                if (hasKey(msg)) {
                    return text(msg);
                }
            }
            cursor = cursor.getCause();
        }
        return text(fallbackKey);
    }
}
