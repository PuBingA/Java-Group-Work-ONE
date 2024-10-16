package CodeChangeMonitor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent;
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class FileChangeListener {

    public FileChangeListener(Project project) {
        project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                for (VFileEvent event : events)
                {
                    // 处理文件变化事件
                    if (event instanceof VFileDeleteEvent) {
                        System.out.println("File deleted: " + event.getFile().getPath());
                    } else if (event instanceof VFileCreateEvent) {
                        System.out.println("File created: " + event.getFile().getPath());
                    } else if (event instanceof VFilePropertyChangeEvent) {
                        System.out.println("File changed: " + event.getFile().getPath());
                    }
                }
            }
        });
    }
}








