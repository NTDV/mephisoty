package ru.valkovets.mephisoty.db.projection.special.file;

import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.projection.complex.CreatedModifiedProj;
import ru.valkovets.mephisoty.db.projection.simple.CommentProj;

/**
 * Projection for {@link File}
 */
public interface FileFullProj extends FileProj, CommentProj, CreatedModifiedProj {
}
