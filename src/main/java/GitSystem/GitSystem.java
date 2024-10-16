package GitSystem;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.io.NullOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.List;

// JGIT
public class GitSystem {
    private Git git;
    private Repository repository;

    // 检查是否有 Git 仓库，如果没有则初始化
    public void checkAndInitRepository(String repoPath) throws IOException, GitAPIException {
        File repoDir = new File(repoPath);

        // 检查是否存在 .git 目录
        if (!new File(repoDir, ".git").exists()) {
            System.out.println("No Git repository found, initializing new repository...");
            git = Git.init().setDirectory(repoDir).call();
        } else {
            System.out.println("Git repository found, opening existing repository...");
            repository = new FileRepositoryBuilder()
                    .setGitDir(new File(repoDir, ".git"))
                    .build();
            git = new Git(repository);
        }
    }

    // 提交当前版本到指定分支
    public void commitToBranch(String branchName, String commitMessage) throws GitAPIException {
        git.checkout().setName(branchName).call();
        git.add().addFilepattern(".").call();
        git.commit().setMessage(commitMessage).call();
        System.out.println("Committed changes to branch: " + branchName);
    }

    // 创建新分支
    public void createBranch(String newBranchName) throws GitAPIException {
        // 创建新分支
        git.branchCreate().setName(newBranchName).call();
        System.out.println("Branch created: " + newBranchName);
    }
   // 获取上一次提交的所有文件
   public void getLatestCommitFiles() throws Exception {
       Iterable<RevCommit> commits = git.log().setMaxCount(1).call(); // 获取最新一次提交
       RevCommit latestCommit = null;

       for (RevCommit commit : commits) {
           latestCommit = commit;
           break; // 只需要获取最新的一次提交
       }

       if (latestCommit != null) {
           RevTree tree = latestCommit.getTree(); // 获取最新提交的树对象

           // 遍历树对象中的所有文件
           try (TreeWalk treeWalk = new TreeWalk(repository)) {
               treeWalk.addTree(tree);
               treeWalk.setRecursive(true); // 递归遍历所有文件

               System.out.println("Files in the latest commit:");
               while (treeWalk.next()) {
                   String filePath = treeWalk.getPathString(); // 获取文件路径
                   System.out.println("File: " + filePath);
               }
           }
       } else {
           System.out.println("No commits found in the repository.");
       }
   }

   // 第一次提交 进行初始化并且提交
   public void initialCommit(String commitMessage) throws GitAPIException, IOException {
       // 将所有文件添加到暂存区
       git.add().addFilepattern(".").call(); // 添加当前目录下的所有文件
       git.commit().setMessage(commitMessage).call(); // 提交
       System.out.println("Initial commit created with message: " + commitMessage);
   }
}
