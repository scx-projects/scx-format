package dev.scx.format.json;

import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.TokenStreamFactory;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.json.JsonWriteFeature;

/// JacksonHelper
///
/// @author scx567888
/// @version 0.0.1
final class JacksonHelper {

    public static int enable(TokenStreamFactory.Feature f, int features) {
        features |= f.getMask();
        return features;
    }

    public static int disable(TokenStreamFactory.Feature f, int features) {
        features &= ~f.getMask();
        return features;
    }

    public static int configure(TokenStreamFactory.Feature f, boolean state, int features) {
        return state ? enable(f, features) : disable(f, features);
    }

    public static int enable(StreamReadFeature f, int features) {
        features |= f.getMask();
        return features;
    }

    public static int disable(StreamReadFeature f, int features) {
        features &= ~f.getMask();
        return features;
    }

    public static int configure(StreamReadFeature f, boolean state, int features) {
        return state ? enable(f, features) : disable(f, features);
    }

    public static int enable(StreamWriteFeature f, int features) {
        features |= f.getMask();
        return features;
    }

    public static int disable(StreamWriteFeature f, int features) {
        features &= ~f.getMask();
        return features;
    }

    public static int configure(StreamWriteFeature f, boolean state, int features) {
        return state ? enable(f, features) : disable(f, features);
    }

    public static int enable(JsonReadFeature f, int features) {
        features |= f.getMask();
        return features;
    }

    public static int disable(JsonReadFeature f, int features) {
        features &= ~f.getMask();
        return features;
    }

    public static int configure(JsonReadFeature f, boolean state, int features) {
        return state ? enable(f, features) : disable(f, features);
    }

    public static int enable(JsonWriteFeature f, int features) {
        features |= f.getMask();
        return features;
    }

    public static int disable(JsonWriteFeature f, int features) {
        features &= ~f.getMask();
        return features;
    }

    public static int configure(JsonWriteFeature f, boolean state, int features) {
        return state ? enable(f, features) : disable(f, features);
    }

}
