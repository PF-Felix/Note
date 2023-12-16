import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import org.junit.Test;

import java.io.*;

public class NoteTest {

    /**
     * 笔记目录是当前项目的父目录
     */
    private final File noteFolder = new File(System.getProperty("user.dir")).getParentFile().getParentFile().getParentFile();

    @Test
    public void test() {
        System.out.println("当前项目的路径：" + noteFolder.getAbsolutePath());
        System.out.println("笔记数量：" + getFiles(noteFolder, ".md").length);
        System.out.println("图片数量：" + getFiles(noteFolder, ".png").length);
    }

    /**
     * 删除无用的图片
     * 执行方法之前备份图片文件夹
     */
    @Test
    public void removeOnUsefulImages() throws IOException {
        //所有图片
        File[] images = getFiles(noteFolder, ".png");
        //所有 markdown 文件
        File[] mdFiles = getFiles(noteFolder, ".md");
        for (File image : images) {
            boolean remove = true;
            //遍历所有 markdown 文件，判断是否包含图片
            xxx:
            for (File mdFile : mdFiles) {
                BufferedReader reader = new BufferedReader(new FileReader(mdFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(image.getName().replaceAll(StrUtil.SPACE, "%20"))) {
                        remove = false;
                        break xxx;
                    }
                }
                reader.close();
            }
            //对于没用的图片就删除了。
            if (remove) {
                image.deleteOnExit();
            }
        }
    }

    /**
     * 获取一个目录下的所有 .md 文件
     */
    private File[] getFiles(File mdFolder, String suffix) {
        FilenameFilter filter = (dir, name) -> name.endsWith(suffix);
        //根目录下的所有文件
        File[] files = ArrayUtil.defaultIfEmpty(mdFolder.listFiles(filter), new File[0]);
        //根目录下的所有文件夹
        File[] subFolders = ArrayUtil.defaultIfEmpty(mdFolder.listFiles(File::isDirectory), new File[0]);
        for (File subFolder : subFolders) {
            File[] subFiles = getFiles(subFolder, suffix);
            if (subFiles.length != 0) {
                File[] tmp = new File[files.length + subFiles.length];
                System.arraycopy(files, 0, tmp, 0, files.length);
                System.arraycopy(subFiles, 0, tmp, files.length, subFiles.length);
                files = tmp;
            }
        }
        return files;
    }
}
