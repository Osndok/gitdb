package github.osndok.gitdb;

import org.buildobjects.process.ProcBuilder;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Unit test for simple App.
 */
public class MainTest
{
    private static final Random RANDOM = new Random();

    public static
    void main(String[] args) throws IOException
    {
        var temp = File.createTempFile("gitdb-", "-MainTest");
        temp.delete();
        temp.mkdir();

        var db = new SingleThreadedDatabase(temp);
        db.initializeGitRepo("unit test", "test@example.com", "MainTest");

        var thing = new Thing();
        thing.subObject = new OtherThing();
        assert thing._db_id == null;

        var trans = db.startTransaction();
        trans.create(thing);

        var id = thing._db_id();
        assert id != null;

        trans.commitPrintF("int %d from main-test", thing.integerPrimitive);

        var firstThingsId = trans.listIds(Thing.class).iterator().next();
        assert firstThingsId.equals(id);

        var cached = trans.get(Thing.class, id);
        assert cached == thing;

        trans = db.startTransaction();

        var refetch = trans.get(Thing.class, id);
        assert refetch != cached;
        assert refetch.integerPrimitive == cached.integerPrimitive;

        var file = db.getFile(Thing.class, id);
        System.out.println(ProcBuilder.run("cat", file.toString()));

        trans.mutate(refetch, SubThing.class);
        trans.commit("mutated");

        var sub = trans.get(SubThing.class, id);

        file = db.getFile(SubThing.class, id);

        sub.recordThing = new RecordThing("a", "b");
        trans.save(sub);
        trans.commit("with record");

        // NOTE: subThingField does not appear in the file, but is accessible. It won't be stable until saved.
        System.out.println(ProcBuilder.run("cat", file.toString()));

        var fileId = trans.putAttachment(file);
        System.out.println("Attachment fileId: " + fileId);
        fileId = trans.putAttachment(new File("pom.xml"));
        System.out.println("Attachment fileId: " + fileId);
        trans.commit("Attachments");

        file = trans.getAttachment(fileId);
        assert file.canRead();

        refetch = trans.get(SubThing.class, id);
        var uuidInDb = refetch.uuid;
        var newUuid = UUID.randomUUID();
        refetch.uuid = newUuid;
        var prestine = refetch._as_fetched_from_db(SubThing.class);
        assert prestine.uuid.equals(uuidInDb);
        trans.update(refetch);
        trans.commit("uuid change");
        prestine = refetch._as_fetched_from_db(SubThing.class);
        assert prestine.uuid.equals(newUuid);

        //System.out.println(ProcBuilder.run("cat", file.toString()));
    }

    static class Thing extends GitDbObject
    {
        public int integerPrimitive = RANDOM.nextInt();
        public Integer integer = 1234;
        public Integer integerNull;
        public UUID uuid = UUID.randomUUID();
        public UUID uuidNull;
        public Date date = new Date();
        public Date dateNull;
        public Duration duration = Duration.of(3, ChronoUnit.DAYS);
        public Duration durationNull;
        public
        Set<UUID> uuidSet = Set.of(UUID.randomUUID(), UUID.randomUUID());
        public
        Map<String, OtherThing> map = Map.of("A", new OtherThing(), "Beta", new OtherThing());
        public
        OtherThing subObject;
    }

    private static
    class OtherThing
    {
        public
        UUID otherThingField = UUID.randomUUID();
    }

    private static
    class SubThing extends Thing
    {
        public
        UUID subThingField = UUID.randomUUID();

        public
        RecordThing recordThing;
    }

    private record RecordThing(String alpha, String beta) {}
}
