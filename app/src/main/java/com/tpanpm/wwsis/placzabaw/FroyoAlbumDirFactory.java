package com.tpanpm.wwsis.placzabaw;

import android.os.Environment;

import java.io.File;

/**
 * Created by Tomasz Zajc on 2017-05-27.
 */

public final class FroyoAlbumDirFactory extends AlbumStorageDirFactory{
    @Override
    public File getAlbumStorageDir(String albumName) {
        // TODO Auto-generated method stub
        return new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                albumName
        );
    }
}
