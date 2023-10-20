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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class PasGenerator {
    public static void main(String[] args) {
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
    }

    public static void UpdatePass(String[] args) {
        try {
            // Загрузить JSON из файла
            Path cluster = Path.of(ClassLoader.getSystemResource("cluster_creation_data.json").toURI());
            String jsonContent = Files.readString(jsonFilePath);

            // Создать ObjectMapper для работы с JSON
            ObjectMapper objectMapper = new ObjectMapper();

            // Разобрать JSON
            JsonNode jsonNode = objectMapper.readTree(jsonContent);

            // Получить объект "userSpecs"
            ObjectNode userSpecsNode = (ObjectNode) jsonNode.at("/userSpecs/0");

            // Заменить значение поля "password" на сгенерированный пароль
            String generatedPassword = generatePassword(); // Здесь должен быть ваш код генерации пароля
            userSpecsNode.put("password", generatedPassword);

            // Записать обновленный JSON обратно в файл
            Files.writeString(jsonFilePath, objectMapper.writeValueAsString(jsonNode));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
