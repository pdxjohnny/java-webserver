package cli;

/**
 * Created by John Andersen on 5/29/16.
 */

import net.carpoolme.db.Database;

public class CLIList extends CLICommand {
    public CLIList(Database mDatabase, String[] mArgv, String mPreviousCommands) {
        super("list", mDatabase, mArgv, mPreviousCommands);
        subCommands = new Object[][]{
                new Object[]{"food", new CLIListFood(database, downArgLevel(), commandsSoFar())},
                new Object[]{"store", new CLIListStore(database, downArgLevel(), commandsSoFar())},
//                new Object[]{"restaurant", new CLIListRestaurant(database, downArgLevel(), commandsSoFar())},
        };
    }
}
