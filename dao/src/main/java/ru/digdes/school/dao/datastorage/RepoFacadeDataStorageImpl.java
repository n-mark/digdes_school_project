package ru.digdes.school.dao.datastorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.digdes.school.dao.facade.RepositoryFacade;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Данная реализация позволяет работать с любым классом хранимого объекта.
 * Поля объекта будут по умолчанию сохранены в формате JSON в '/datastorage/ИмяКласса'
 * в корневой директории проекта. Методы определяют нужную директорию
 * используя .getSimpleName() переданного класса, имя файла соответствует 'id',
 * соответственно, у класса с которым работает данная реализация должно быть поле 'id' типа Long.
 */
public class RepoFacadeDataStorageImpl<T> implements RepositoryFacade<T> {
    private final String workdir;
    private final Class<T> tClass;
    private final AtomicLong idGenerator = new AtomicLong(0L);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = Logger.getLogger(RepoFacadeDataStorageImpl.class.getName());

    public RepoFacadeDataStorageImpl(Class<T> tClass) {
        this.tClass = tClass;
        //Set the working directory to the root of the current project
        this.workdir = System.getProperty("user.dir") + "/" + "datastorage" + "/" + tClass.getSimpleName();
    }

    @Override
    public T create(T object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            ObjectNode objectNode =  objectMapper.readValue(json, ObjectNode.class);
            if (!objectNode.has("id")) {
                throw new IllegalArgumentException("Can't define the 'id' field in the persistence object");
            };
            objectNode.put("id", idGenerator.incrementAndGet());

            byte[] data = objectNode.toString().getBytes();

            Path filePath = Path.of(workdir + "/" + objectNode.get("id").toString() + ".json");
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, data);

            logger.info("The file " + filePath + " has been written.");
            return getObjectFromFile(filePath);
        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T getById(Long id) {
        String fileName = id.toString();
        Path filePath = Path.of(workdir + "/" + id.toString() + ".json");

        try {
            if (!Files.exists(filePath)) {
                throw new IllegalArgumentException("The file with 'id' = " + id + " doesn't exist");
            }
            return getObjectFromFile(filePath);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<T> getAll() {
        try (Stream<Path> paths = Files.list(Path.of(workdir))) {
            return paths.filter(Files::isRegularFile)
                    .map(this::getObjectFromFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T update(T object) {
        try {
            JsonNode updateNode = objectMapper.valueToTree(object);
            idCheck(updateNode);
            String fileName = workdir + "/" + updateNode.get("id").toString() + ".json";

            Path path = Path.of(fileName);
            byte[] jsonData = Files.readAllBytes(path);

            JsonNode rootNode = objectMapper.readTree(jsonData);
            ObjectNode existingNode = (ObjectNode) rootNode;

            //Getting only non-null values from the update object and set them to the requested object
            updateNode.fields().forEachRemaining(entry -> {
                String fieldName = entry.getKey();
                JsonNode fieldValue = entry.getValue();
                if (!fieldValue.isNull()) {
                    existingNode.replace(fieldName, fieldValue);
                }
            });

            Files.write(path, objectMapper.writeValueAsBytes(rootNode));
            logger.info("The file " + path + " has been updated.");

            return getObjectFromFile(path);
        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String filePath = workdir + "/" + id.toString() + ".json";
        try {
            Path path = Paths.get(filePath);
            Files.delete(path);
            logger.info("The file " + path + " has been deleted.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void idCheck(JsonNode jsonNode) throws IllegalArgumentException {
        if (!jsonNode.has("id")) {
            throw new IllegalArgumentException("Can't define the 'id' field in the persistence object");
        }
        if (jsonNode.get("id").isNull()) {
            throw new IllegalArgumentException("'id' cannot be null");
        }
    }

    private T getObjectFromFile(Path path) {
        try {
            return objectMapper.readValue(Files.readAllBytes(path), tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
