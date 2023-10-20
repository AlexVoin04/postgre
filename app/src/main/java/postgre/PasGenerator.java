package postgre;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.passay.CharacterData;
import org.passay.PasswordGenerator;
import org.passay.PasswordData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.RuleResult;

import javax.annotation.processing.Generated;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class PasGenerator {
    private static String Generate() {
        PasswordGenerator passwordGenerator = new PasswordGenerator();

        // Правила генерации
        CharacterRule upperCaseRule = new CharacterRule(EnglishCharacterData.UpperCase, 1);
        CharacterRule lowerCaseRule = new CharacterRule(EnglishCharacterData.LowerCase, 1);
        CharacterRule digitRule = new CharacterRule(EnglishCharacterData.Digit, 1);
        CharacterRule specialCharRule = new CharacterRule(
            new CharacterData() {
                public String getErrorCode() {
                    return "ERROR_CODE";
                }

                public String getCharacters() {
                    return "!@#$%^&*_-/.<>|";
                }
            }
        );

        String password = passwordGenerator.generatePassword(10, Arrays.asList(
                upperCaseRule,
                lowerCaseRule,
                digitRule,
                specialCharRule
        ));
        System.out.println("Password: " + password);
        return password;
    }

    public static void UpdatePass() {
        try {
            // JSON файл
            Path cluster = Path.of(ClassLoader.getSystemResource("cluster_creation_data.json").toURI());
            String jsonContent = Files.readString(cluster);

            // ObjectMapper для работы с JSON
            ObjectMapper objectMapper = new ObjectMapper();

            // "Разборка" JSON
            JsonNode jsonNode = objectMapper.readTree(jsonContent);

            // Получение объект "userSpecs"
            ObjectNode userSpecsNode = (ObjectNode) jsonNode.at("/userSpecs/0");

            // Замена "password" на сгенерированный пароль
            String generatedPassword = Generate();
            userSpecsNode.put("password", generatedPassword);
            System.out.println("Json\n" + userSpecsNode);

            // Запись обратно в файл
            Files.writeString(cluster, objectMapper.writeValueAsString(jsonNode));
            System.out.println("Json\n" + jsonNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
