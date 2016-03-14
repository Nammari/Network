/**
 * 
 */
package nammari.network.loader;

/**
 * 
 * 
 * 
 * @author Nammari
 * 
 */
public interface ErrorAwareLoader {

	public int getLoaderId();

	public boolean containsError();

	public void retryTask();

	public String getErrorMessage();

	public boolean isLoading();

}
