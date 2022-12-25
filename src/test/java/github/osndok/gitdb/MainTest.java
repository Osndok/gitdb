package github.osndok.gitdb;

import github.osndok.gitdb.pathing.StupidlySimplePathing;
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
        db.initializeGitRepo("MainTest");

        var thing = new Thing();
        thing.subObject = new SubThing();
        assert thing._db_id == null;

        var trans = db.startTransaction();
        trans.create(thing);

        var id = thing._db_id();
        assert id != null;

        trans.commit("int %d from main-test", thing.integerPrimitive);

        assert trans.listIds(Thing.class).iterator().next().equals(id);

        var cached = trans.get(Thing.class, id);
        assert cached == thing;

        trans = db.startTransaction();

        var refetch = trans.get(Thing.class, id);
        assert refetch != cached;
        assert refetch.integerPrimitive == cached.integerPrimitive;

        var file = db.getFile(id, Thing.class);
        System.out.println(ProcBuilder.run("cat", file.toString()));
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
        Map<String, SubThing> map = Map.of("A", new SubThing(), "Beta", new SubThing());
        public
        SubThing subObject;
    }

    private static
    class SubThing
    {
        public
        UUID subThingField = UUID.randomUUID();
    }
}
