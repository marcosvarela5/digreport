package es.marcos.digreport.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindImage {
    private Long id;
    private Long findId;
    private String url;

    public FindImage() {}

    public FindImage(Long id, Long findId, String url) {
        this.id = id;
        this.findId = findId;
        this.url = url;
    }
}