package github.osndok.gitdb;

import github.osndok.gitdb.pathing.StupidlySimplePathing;

import java.io.File;
import java.io.IOException;
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

        var db = new SingleThreadedDatabase(temp, new StupidlySimplePathing());
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
        assert refetch.luckyNumber == cached.luckyNumber;
    }

    static class Thing extends GitDbObject
    {
        public int luckyNumber = RANDOM.nextInt();
        public UUID extraUuid = UUID.randomUUID();
        public Date importantDate = new Date();
    }
}
