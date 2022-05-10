/**
 *
 *  @author Klik Konrad S22885
 *
 */

package zad1;


import org.yaml.snakeyaml.Yaml;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Tools {
    static Options createOptionsFromYaml(String fileName) throws Exception {
        Yaml yaml = new Yaml();
        Map<String, Object> obj = yaml.load(Files.newInputStream(Paths.get(fileName)));
        return new Options((String) obj.get("host"),(int)obj.get("port"), (Boolean) obj.get("concurMode"),
                (Boolean) obj.get("showSendRes"), (Map<String, List<String>>) obj.get("clientsMap"));
    }
}
