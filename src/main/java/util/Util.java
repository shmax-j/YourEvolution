package util;

import controllers.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

public class Util {
    public static URI resource(String path) throws URISyntaxException {
        return Main.class.getClassLoader().getResource(path).toURI();
    }

    public static File fileResource(String path) throws URISyntaxException {
        return new File(resource(path));
    }

    public static FileInputStream fisResource(String path) {
        try {
            return new FileInputStream(fileResource(path));
        } catch (FileNotFoundException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
