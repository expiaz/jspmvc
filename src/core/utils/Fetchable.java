package core.utils;

/**
 * Represents a class which can be fetched from another
 * The class returned from ::from() will implements core.utils.Fetcher::fetch(core.annontations.Fetchable)
 */
public interface Fetchable {
    Class<? extends Fetcher> from();
}