package com.artemsirosh.lite.sftp.service;

import com.artemsirosh.lite.sftp.domain.Item;
import com.artemsirosh.lite.sftp.domain.ItemId;
import com.artemsirosh.lite.sftp.port.outbound.CreateFilePort;
import com.artemsirosh.lite.sftp.port.outbound.GetFileContentPort;
import com.artemsirosh.lite.sftp.port.outbound.GetItemByIdPort;
import com.artemsirosh.lite.sftp.port.outbound.RegisterItemPort;
import com.artemsirosh.lite.sftp.domain.File;
import com.artemsirosh.lite.sftp.domain.FileContent;
import com.artemsirosh.lite.sftp.port.inbound.CreateFileCommand;
import com.artemsirosh.lite.sftp.port.inbound.CreateFileUseCase;
import com.artemsirosh.lite.sftp.port.inbound.GetFileContentQuery;
import com.artemsirosh.lite.sftp.port.inbound.GetFileContentUseCase;
import com.artemsirosh.lite.sftp.port.inbound.UpdateFileCommand;
import com.artemsirosh.lite.sftp.port.inbound.UpdateFileUseCase;
import com.artemsirosh.lite.sftp.port.outbound.UpdateFilePort;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class FileService implements CreateFileUseCase, GetFileContentUseCase, UpdateFileUseCase {

    private final CreateFilePort createFilePort;
    private final GetItemByIdPort getItemByIdPort;
    private final RegisterItemPort registerItemPort;
    private final GetFileContentPort getFileContentPort;
    private final UpdateFilePort updateFilePort;

    @Override
    @NonNull
    public File createFile(@NonNull final CreateFileCommand command) {
        final Item parentItem = getItemByIdPort.getItemById(command.getParentId());

        if (parentItem.isDirectory()) {
            final File file = File.builder()
                    .id(ItemId.newInstanceUUID())
                    .name(command.getName())
                    .parent(parentItem)
                    .modifiedDate(command.getLastModified())
                    .build();

            final var content = new FileContent(command.getContent());
            createFilePort.createFile(file, content);
            registerItemPort.register(file);
            return file;
        } else {
            throw new ServiceException("Parent item should be directory");
        }

    }

    @Override
    @NonNull
    public FileContent getFileContent(@NonNull final GetFileContentQuery query) {
        final Item file = getItemByIdPort.getItemById(query.getFileId());
        if (!file.isDirectory()) {
            return getFileContentPort.getFileContent((File) file);
        } else {
            throw new ServiceException("Item with id '" + query.getFileId() + "' isn't a file");
        }
    }

    @Override
    @NonNull
    public File updateFile(@NonNull UpdateFileCommand command) {
        final Item item = getItemByIdPort.getItemById(command.getFileId());
        if (!item.isDirectory()) {
            final File existedFile = (File) item;
            final var updatedFile = File.builder()
                    .id(existedFile.getId())
                    .parent(existedFile.getParent())
                    .modifiedDate(command.getLastModified().orElse(existedFile.getModifiedDate()))
                    .name(command.getName().orElse(existedFile.getName()))
                    .build();

            updateFilePort.updateFile(updatedFile);
            if (command.getContent().isPresent()) {
                updateFilePort.updateFileContent(new FileContent(command.getContent().get()));
            }

            registerItemPort.register(updatedFile);
            return updatedFile;

        } else {
            throw new ServiceException("Item with id '" + command.getFileId() + "' isn't a file");
        }
    }
}
