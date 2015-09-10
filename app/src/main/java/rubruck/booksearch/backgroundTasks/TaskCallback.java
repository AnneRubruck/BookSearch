package rubruck.booksearch.backgroundTasks;

/**
 * A simple interface to introduce a method to be called,
 * after an asynchronous task is finished.
 * Created by rubruck on 26/08/15.
 */
public interface TaskCallback
{
    void done();
}
