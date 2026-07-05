ALTER TABLE cashback_record
    ADD COLUMN posted_at TIMESTAMP WITH TIME ZONE NOT NULL;

CREATE INDEX idx_cashback_record_posted_at ON cashback_record (posted_at);
