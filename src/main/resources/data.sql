INSERT INTO `mynlife_dev`.`hobby`
(`id`,
 `large_category`,
 `medium_category`,
 `small_category`,
 `name`,
 `main_color`,
 `main_image`)
VALUES
    (1,
        '레저스포츠',
        '무언가',
        '오토바이',
        '모터싸이클',
        'color',
        'https://jasonryunet.files.wordpress.com/2021/06/2021-bmw-motorrad-m-1000-rr-01.jpg?w=1440');

INSERT INTO `mynlife_dev`.`hobby`
(`id`,
 `large_category`,
 `medium_category`,
 `small_category`,
 `name`,
 `main_color`,
 `main_image`)
VALUES
    (2,
     '레저스포츠',
     '무언가',
     '겨울',
     '스키&보드',
     'color2',
     'https://img.olympicchannel.com/images/image/private/t_16-9_3200/primary/zvfbyk6kasvsentgp8u4');

-- 골프도 넣자~

INSERT INTO `mynlife_dev`.`into_hobby_cost`
(`hobby_id`,
 `user_id`,
 `into_hobby_cost_data`)
VALUES
    (1,
     2,
     '{"헬맷":600000, "보험료":800000, "바이크": 3800000}'
    );

INSERT INTO `mynlife_dev`.`into_hobby_cost`
(`hobby_id`,
 `user_id`,
 `into_hobby_cost_data`)
VALUES
    (2,
     2,
     '{"골프채":3000000, "멤버쉽":100000}'
    );
