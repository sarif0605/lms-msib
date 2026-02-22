-- Drop existing constraints
ALTER TABLE user_chapter_progress DROP FOREIGN KEY fk_progress_chapter;
ALTER TABLE quizzes DROP FOREIGN KEY fk_quiz_chapter;

-- Re-add with CASCADE DELETE
ALTER TABLE user_chapter_progress 
ADD CONSTRAINT fk_progress_chapter 
FOREIGN KEY (chapter_id) 
REFERENCES chapters(id) 
ON DELETE CASCADE;

ALTER TABLE quizzes 
ADD CONSTRAINT fk_quiz_chapter 
FOREIGN KEY (chapter_id) 
REFERENCES chapters(id) 
ON DELETE CASCADE;
