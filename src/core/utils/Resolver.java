package core.utils;

import java.util.Arrays;

public class Resolver {

    /**
     * check if an interface is present either on the class or it's parents
     * @param search
     * @param in
     * @return
     */
    public static boolean isInterfacePresent(Class search, Class in) {
        do{
            if(Arrays.asList(in.getInterfaces()).contains(search)) {
                return true;
            }
        } while((in = in.getSuperclass()) != null);
        return false;
    }

}
