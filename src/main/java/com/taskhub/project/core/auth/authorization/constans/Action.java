package com.taskhub.project.core.auth.authorization.constans;

import lombok.Getter;

import java.util.List;

@Getter
public enum Action {
    EDIT_WORKSPACE("EDIT_WORKSPACE"),
    MANAGE_USER("MANAGE_USER"),
    EDIT_ROLE("EDIT_ROLE"),
    EDIT_BOARD("EDIT_BOARD"),
    DELETE_BOARD("DELETE_BOARD"),
    EDIT_CARD_TEMPLATE("EDIT_CARD_TEMPLATE"),
    EDIT_CARD("EDIT_CARD");

    public final String code;

    Action(String code) {
        this.code = code;
    }

    public static boolean contains(String item) {
        for (Action action : Action.values()) {
            if (action.code.equals(item)) {
                return true;
            }
        }
        return false;
    }

    public static boolean validateAction(List<String> actions) {
        var resp = false;
        for (var action : actions) {
            if (!Action.contains(action)) {
                resp = true;
                break;
            }
        }
        return resp;
    }
}
