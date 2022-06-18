CREATE TABLE IF NOT EXISTS item_detail_image
(
    id        BIGINT(20)    NOT NULL AUTO_INCREMENT,
    item      BIGINT(20)    NULL,
    imageUrl  VARCHAR(3000) NOT NULL,

    createdAt DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    isDeleted TINYINT(1)    NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id),
    CONSTRAINT item_detail_image_item_fk FOREIGN KEY (item) REFERENCES item (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);