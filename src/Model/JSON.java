package Model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public abstract class JSON<T> {
    public String toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        String s = "";
        try {
            s = mapper.writeValueAsString(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static T read(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        T t = null;
        try {
            t = mapper.readValue(jsonString, T.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }
}
