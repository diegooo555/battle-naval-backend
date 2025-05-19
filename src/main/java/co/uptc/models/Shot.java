package co.uptc.models;

import co.uptc.constants.ShotResult;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Shot {
    protected Position position;
    protected ShotResult result;
}
