package service;

import java.security.InvalidParameterException;

public class NoteService {

    public float isNoteValid(String value) {
        try {
            float v = Float.valueOf(value);
            if (v < 0 || v > 20) {
                return 0;
            }
            return v;
        } catch (Exception e) {
            return 0;
        }
    }

    public float isNoteValid(String value, boolean throwing) throws InvalidParameterException {
        float v = this.isNoteValid(value);
        if (v == 0) {
            throw new InvalidParameterException(v + " n'est pas une note valide");
        }
        return v;
    }

}
