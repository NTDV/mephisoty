package ru.valkovets.mephisoty.db.projection.special.file;

import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.projection.complex.IdCommentProj;
import ru.valkovets.mephisoty.settings.FileAccessPolicy;

/**
 * Projection for {@link File}
 */
public interface FileProj extends IdCommentProj {
String getOriginalName();
String getCode();
FileAccessPolicy getAccessPolicy();
}
