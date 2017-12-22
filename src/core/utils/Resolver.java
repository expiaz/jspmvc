package core.utils;

import java.util.Arrays;

public class Resolver {

    public static boolean isInterfacePresent(Class search, Class in) {
        do{
            if(Arrays.asList(in.getInterfaces()).contains(search)) {
                return true;
            }
        } while((in = in.getSuperclass()) != null);
        return false;
    }

}
