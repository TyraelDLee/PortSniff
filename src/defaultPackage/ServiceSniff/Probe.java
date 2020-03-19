package defaultPackage.ServiceSniff;

/**
 * This probe class sending TCP/UDP packages to
 * target to get the fingerprint of target.
 * */
public class Probe {
    private String protocol;

    Probe(String protocol){
        this.protocol = protocol;
    }
}
