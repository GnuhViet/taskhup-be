package com.taskhub.project.common.socket.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SocketResponse<T, A> {
    private T data;
    private A action;
}
