package com.taskhub.project.common;

public class Constants {
    public enum ServiceStatus {
        SUCCESS,
        ERROR
    }

    public static class ActionString {
        public static final String EDIT_WORKSPACE = "EDIT_WORKSPACE";
        public static final String MANAGE_USER = "MANAGE_USER";
        public static final String EDIT_ROLE = "EDIT_ROLE";
        public static final String EDIT_BOARD = "EDIT_BOARD";
        public static final String DELETE_BOARD = "DELETE_BOARD";
        public static final String ASSIGN_MEMBER = "ASSIGN_MEMBER";
        public static final String EDIT_CARD_TEMPLATE = "EDIT_CARD_TEMPLATE";
        public static final String EDIT_CARD = "EDIT_CARD";
    }

    public static class CustomFieldTypes {
        public static final String TEXT = "TEXT";
        public static final String DATE = "DATE";
        public static final String CHECKBOX = "CHECKBOX";
        public static final String DROPDOWN = "DROPDOWN";
    }
}
