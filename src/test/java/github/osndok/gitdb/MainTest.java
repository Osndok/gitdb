package github.osndok.gitdb;

import github.osndok.gitdb.pathing.StupidlySimplePathing;
import org.buildobjects.process.ProcBuilder;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

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
        db.initializeGitRepo();

        var thing = new Thing();
        assert thing._db_id == null;

        var trans = db.startTransaction();
        trans.create(thing);

        var id = thing._db_id();
        assert id != null;

        trans.commit("from main-test");

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
    }
}
