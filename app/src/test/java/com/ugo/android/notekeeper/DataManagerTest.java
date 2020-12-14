package com.ugo.android.notekeeper;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DataManagerTest {

    @Before
    public void setUp() throws Exception {
        DataManager dataManager = DataManager.getInstance();
        dataManager.getNotes().clear();
        dataManager.initializeExampleNotes();
    }

    @Test
    public void testCreateNewNote() throws Exception {
        DataManager dataManager = DataManager.getInstance();
        final CourseInfo course = dataManager.getCourse("android_async");
        final String noteTitle = "Test note title";
        final String noteText = "This is the body text of my test note";

        int noteIndex = dataManager.createNewNote();
        NoteInfo newNote = dataManager.getNotes().get(noteIndex);
        newNote.setCourse(course);
        newNote.setTitle(noteTitle);
        newNote.setText(noteText);

        NoteInfo compareNote = dataManager.getNotes().get(noteIndex);
        Assert.assertEquals(compareNote.getCourse(), course);
        Assert.assertEquals(compareNote.getTitle(), noteTitle);
        Assert.assertEquals(compareNote.getText(), noteText);
    }

    @Test
    public void findSimilarNotes() throws Exception {
        DataManager dataManager = DataManager.getInstance();
        final CourseInfo course = dataManager.getCourse("android_async");
        final String noteTitle = "Test note title";
        final String noteText1 = "This is the body text of my test note";
        final String noteText2 = "This is the body of my second test note";

        int noteIndex1 = dataManager.createNewNote();
        NoteInfo newNote1 = dataManager.getNotes().get(noteIndex1);
        newNote1.setCourse(course);
        newNote1.setTitle(noteTitle);
        newNote1.setText(noteText1);

        int noteIndex2 = dataManager.createNewNote();
        NoteInfo newNote2 = dataManager.getNotes().get(noteIndex2);
        newNote2.setCourse(course);
        newNote2.setTitle(noteTitle);
        newNote2.setText(noteText2);

        int foundIndex1 = dataManager.findNote(newNote1);
        Assert.assertEquals(noteIndex1, foundIndex1);

        int foundIndex2 = dataManager.findNote(newNote2);
        Assert.assertEquals(noteIndex2, foundIndex2);
    }

    @Test
    public void createNewNoteOneStepCreation() {
        DataManager dataManager = DataManager.getInstance();
        final CourseInfo course = dataManager.getCourse("android_sync");
        final String  noteTitle = "Test note title";
        final String noteText = "This is the body of my test note";

        int noteIndex = dataManager.createNewNote(course, noteTitle, noteText);

        NoteInfo compareNote = dataManager.getNotes().get(noteIndex);
        Assert.assertEquals(course, compareNote.getCourse());
        Assert.assertEquals(noteTitle, compareNote.getTitle());
        Assert.assertEquals(noteText, compareNote.getText());
    }
}