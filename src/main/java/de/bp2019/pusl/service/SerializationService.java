package de.bp2019.pusl.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vaadin.flow.server.VaadinSession;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.State;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.ExerciseSchemeRepository;
import de.bp2019.pusl.repository.GradeRepository;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.LectureRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.util.Utils;

@Service
public class SerializationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerializationService.class);

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LectureRepository lectureRepository;

    @Autowired
    InstituteRepository instituteRepository;

    @Autowired
    ExerciseSchemeRepository exerciseSchemeRepository;

    public void serialize(OutputStream outputStream, VaadinSession vaadinSession) {
        if (vaadinSession != null) {
            vaadinSession.lock();
            Authentication authentication = vaadinSession.getAttribute(Authentication.class);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            vaadinSession.unlock();
        }


        try {
            GZIPOutputStream gos = new GZIPOutputStream(outputStream);
            JsonGenerator generator = getObjectMapper().createGenerator(gos);

            State state = new State();

            state.setLectures(lectureRepository.findAll());
            state.setUsers(userRepository.findAll());
            state.setInstitutes(instituteRepository.findAll());
            state.setExerciseSchemes(exerciseSchemeRepository.findAll());
            state.setGrades(gradeRepository.findAll());

            generator.writeObject(state);
            generator.close();
        } catch (IOException e) {
            // TODO: ja genau...
        }
    }

    public void loadState(InputStream inputStream) {
        try {
            GZIPInputStream gis = new GZIPInputStream(inputStream);
            JsonParser parser = getObjectMapper().createParser(gis);

            State state = parser.readValueAs(State.class);

            lectureRepository.deleteAll();
            userRepository.deleteAll();
            gradeRepository.deleteAll();
            exerciseSchemeRepository.deleteAll();
            instituteRepository.deleteAll();

            lectureRepository.insert(state.getLectures());
            userRepository.insert(state.getUsers());
            gradeRepository.insert(state.getGrades());
            exerciseSchemeRepository.insert(state.getExerciseSchemes());
            instituteRepository.insert(state.getInstitutes());

        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
    }

    public void insertState(InputStream inputStream) {
        LOGGER.debug("inserting state");

        try {
            GZIPInputStream gis = new GZIPInputStream(inputStream);
            JsonParser parser = getObjectMapper().createParser(gis);

            State state = parser.readValueAs(State.class);

            List<Lecture> lectures = state.getLectures();
            if(lectures != null){
                lectureRepository.insert(state.getLectures());
            }

            List<User> users = (state.getUsers());
            if(users != null){
                userRepository.insert(users);
            }

            List<Grade> grades = state.getGrades();
            if(grades != null) {
                Utils.batches(grades, 1000).forEach(gradesBatch -> {
                    gradeRepository.insert(gradesBatch);
                });
            }

            List<ExerciseScheme> exerciseSchemes = state.getExerciseSchemes();
            if(exerciseSchemes != null) {
                exerciseSchemeRepository.insert(exerciseSchemes);
            }

            List<Institute> institutes = state.getInstitutes();
            if(institutes != null){
                instituteRepository.insert(state.getInstitutes());
            }

        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
    }

    private ObjectMapper getObjectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();

        SimpleModule module = new SimpleModule("ObjectIdSerializer", new Version(1, 0, 0, null, null, null));
        module.addSerializer(ObjectId.class, new ObjectIdSerializer());
        module.addDeserializer(ObjectId.class, new ObjectIdDeserializer());

        objectMapper.registerModule(module);
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }

    public class ObjectIdSerializer extends StdSerializer<ObjectId> {

        public ObjectIdSerializer() {
            this(null);
        }

        public ObjectIdSerializer(Class<ObjectId> t) {
            super(t);
        }

        @Override
        public void serialize(ObjectId id, JsonGenerator jsonGenerator, SerializerProvider serializer)
                throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("stringValue", id.toString());
            jsonGenerator.writeEndObject();
        }
    }

    public class ObjectIdDeserializer extends StdDeserializer<ObjectId> {

        public ObjectIdDeserializer() {
            this(null);
        }

        public ObjectIdDeserializer(Class<ObjectId> t) {
            super(t);
        }

        @Override
        public ObjectId deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {

            ObjectCodec codec = parser.getCodec();
            JsonNode node = codec.readTree(parser);
            
            // try catch block
            JsonNode valueNode = node.get("stringValue");

            return new ObjectId(valueNode.asText());
        }
    }
}
