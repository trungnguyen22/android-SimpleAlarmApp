package com.example.dell.alarm.enums;

public enum EDatabaseOperation {
    INSERT_ITEM_TO_DB("INSERT_ITEM_TO_DB"),
    UPDATE_ITEM_TO_DB("UPDATE_ITEM_TO_DB"),
    DELETE_ITEM_TO_DB("DELETE_ITEM_FROM_DB"),
    GET_ITEM_FROM_DB("GET_ITEM_FROM_DB"),
    GET_LIST_FROM_DB("GET_LIST_FROM_DB");

    String type;

    EDatabaseOperation(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
