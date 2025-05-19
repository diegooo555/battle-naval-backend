package co.uptc.dtos;

import lombok.Getter;

@Getter
public class InfoMessage {
    private String content;

    public InfoMessage(String content) {
        this.content = content;
    }
}
