package com.taskhub.project.comon.socket.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SocketResponse<T, A> {
    private T data;
    private A action;
}
