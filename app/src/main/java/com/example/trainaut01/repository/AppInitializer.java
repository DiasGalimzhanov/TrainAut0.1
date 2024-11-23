package com.example.trainaut01.repository;

import com.example.trainaut01.enums.FineMotorMuscleGroup;
import com.example.trainaut01.enums.GrossMotorMuscleGroup;
import com.example.trainaut01.enums.MotorSkillGroup;
import com.example.trainaut01.enums.WeekDay;
import com.example.trainaut01.models.DayPlan;
import com.example.trainaut01.models.Exercise;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class AppInitializer {
    private final ExerciseRepository exerciseRepository;
    private final DayPlanRepository dayPlanRepository;
//    private final TrainingPlanRepository trainingPlanRepository;

    private List<Exercise> exercisesGrossMotor;
    private List<Exercise> exercisesFineMotor;

    @Inject
    public AppInitializer(ExerciseRepository exerciseRepository, DayPlanRepository dayPlanRepository) {
        this.exerciseRepository = exerciseRepository;
        this.dayPlanRepository = dayPlanRepository;
//        this.trainingPlanRepository = trainingPlanRepository;
        this.exercisesGrossMotor = new ArrayList<>();
        this.exercisesFineMotor = new ArrayList<>();
    }

    public void initializeExercises(OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {

        exercisesGrossMotor.add(new Exercise("1", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.BICEPS, "Сгибание рук стоя с гантелями", "",
                "1. Возьми в каждую руку по гантели, ладони направлены вперед. \n\n" +
                        "2. Встань прямо, ноги на ширине плеч, спина ровная. \n\n" +
                        "3. Медленно сгибай руки в локтях, поднимая гантели к плечам. \n\n" +
                        "4. Когда гантели будут у плеч, остановись на секунду. \n\n" +
                        "5. Затем медленно опусти гантели обратно, выпрямляя руки. \n\n" +
                        "Не спеши, важно выполнять упражнение медленно и аккуратно.",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", "", false, 2, 10, 5, 0, 2));

        exercisesGrossMotor.add(new Exercise("2", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.BICEPS, "Сгибание рук, сидя с упором в бедро", "",
                "1. Сядь на стул или скамейку, поставь ноги широко. \n\n" +
                        "2. Возьми гантель в одну руку, а локоть этой руки поставь на бедро, чтобы рука была уперта. \n\n" +
                        "3. Другая рука может лежать на другом бедре для баланса. \n\n" +
                        "4. Медленно поднимай гантель, сгибая руку в локте, пока гантель не будет близко к плечу. \n\n" +
                        "5. Остановись на секунду, затем медленно опусти гантель обратно, выпрямляя руку. \n\n" +
                        "При завершении подхода меняй руку.\n" +
                        "Выполняй упражнение медленно и следи за тем, чтобы не напрягать спину.",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", "", false, 2, 10, 5, 0, 2));

        exercisesGrossMotor.add(new Exercise("3", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.BICEPS, "Сгибание рук стоя со штангой", "",
                "1. Возьми штангу двумя руками, ладони направлены вперед, руки на ширине плеч. \n\n" +
                        "2. Встань прямо, ноги на ширине плеч, спина ровная. \n\n" +
                        "3. Медленно сгибай руки в локтях, поднимая штангу к груди. \n\n" +
                        "4. Когда штанга будет у груди, остановись на секунду. \n\n" +
                        "5. Затем медленно опусти штангу обратно, выпрямляя руки. \n\n" +
                        "Не спеши, важно выполнять упражнение медленно, чтобы не напрягать спину.",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", "", false, 2, 10, 7, 0, 2));

        exercisesGrossMotor.add(new Exercise("4", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.BICEPS, "Сгибание рук стоя в стороны с гантелями", "",
                "1. Возьми в каждую руку по гантели, ладони направлены к телу.\n\n" +
                        "2. Встань прямо, ноги на ширине плеч, спина ровная.\n\n" +
                        "3. Руки опущены вдоль тела, локти слегка согнуты.\n\n" +
                        "4. Медленно сгибай руки в локтях, поднимая гантели к плечам, ладони по-прежнему направлены внутрь.\n\n" +
                        "5. Остановись на секунду, почувствуй напряжение в бицепсах.\n\n" +
                        "6. Медленно опусти руки обратно вниз, сохраняя контроль.\n\n" +
                        "Не забывай держать локти неподвижными и не спешить, следи за тем, чтобы спина оставалась ровной.",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", "", false, 2, 6, 7, 0, 2));

        exercisesGrossMotor.add(new Exercise("5", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.PECTORAL_MUSCLES, "Жим штанги лёжа", "",
                "1. Ляг на скамью, держи штангу над собой.\n\n" +
                        "2. Опусти штангу к груди, сгибая локти.\n\n" +
                        "3. Поднимай штангу обратно вверх.",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", "", false, 2, 6, 6, 0, 2));

        exercisesGrossMotor.add(new Exercise("6", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.PECTORAL_MUSCLES, "Развод гантелей на скамье", "",
                "1. Ляг на скамью, держи гантели над собой. \n\n" +
                        "2. Медленно разводи руки в стороны, не сгибая локти. \n\n" +
                        "3. Верни руки обратно в исходное положение.",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", "", false, 2, 4, 7, 0, 2));

        exercisesGrossMotor.add(new Exercise("7", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.PECTORAL_MUSCLES, "Жим гантелей лёжа", "",
                "1. Ляг на скамью, держи гантели над грудью. \n\n" +
                        "2. Опусти гантели к груди, сгибая локти. \n\n" +
                        "3. Поднимай гантели обратно вверх.",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", "", false, 2, 5, 5, 0, 2));

        exercisesGrossMotor.add(new Exercise("8", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.PECTORAL_MUSCLES, "Отжимания широким хватом", "",
                "1. Встань в упор лежа, руки шире плеч. \n\n" +
                        "2. Опусти тело вниз, сгибая локти.\n\n" +
                        "3. Поднимай тело обратно вверх.",
                "https://avatars.dzeninfra.ru/get-zen_doc/1101877/pub_5b8e12ed68517200a9a933a8_5b8e17a7e3cd4f00aad66efe/scale_1200", "", false, 2, 4, 6, 0, 2));

        exercisesGrossMotor.add(new Exercise("9", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.TRICEPS, "Отжимания от лавки на трицепс", "",
                "1. Сядь на край лавки и положи руки на нее, ладони смотрят вперед. \n\n" +
                        "2. Спусти бедра с лавки. \n\n" +
                        "3. Медленно сгибай руки в локтях, опуская тело вниз. \n\n" +
                        "4. Поднимай тело обратно вверх, выпрямляя руки. ",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", "", false, 2, 5, 7, 0, 2));

        exercisesGrossMotor.add(new Exercise("10", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.TRICEPS, "Французский жим гантелей лёжа", "",
                "1. Ляг на скамью, держи одну гантель обеими руками над головой. \n\n" +
                        "2. Медленно сгибай руки в локтях, опуская гантель за голову. \n\n" +
                        "3. Поднимай гантель обратно, выпрямляя руки.",
                "https://i.pinimg.com/736x/c1/8b/9b/c18b9b6f5464354b487c88a19bb5a698.jpg", "", false, 2, 6, 7, 0, 2));

        exercisesGrossMotor.add(new Exercise("11", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.TRICEPS, "Жим гантелей из-за головы сидя", "",
                "1. Сядь ровно и держи гантель обеими руками за головой. \n\n" +
                        "2. Поднимай гантель вверх, выпрямляя руки. \n\n" +
                        "3. Опусти гантель обратно за голову.",
                "https://avatars.dzeninfra.ru/get-zen_doc/4457333/pub_621fa6cb0728400008572843_621fa8245529cb2055a456fe/scale_1200", "", false, 3, 4, 6, 0, 2));

        exercisesGrossMotor.add(new Exercise("12", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.TRICEPS, "Жим гантелей с переносом центра тяжести на лавку", "",
                "1. Ляг спиной на лавку, ноги на полу, держи гантели в обеих руках.\n\n" +
                        "2. Перенеси вес тела на плечи, чтобы лучше почувствовать баланс.\n\n" +
                        "3. Подними гантели вверх от груди, выпрямляя руки.\n\n" +
                        "4. Медленно опусти гантели обратно к груди.\n\n" +
                        "5. Дыши ровно и не спеши.",
                "https://i.pinimg.com/originals/e4/c5/78/e4c578bdb976eb75e61d0d9c38c766d0.jpg", "", false, 2, 6, 7, 0, 2));


        exercisesGrossMotor.add(new Exercise("13", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.DELTOID_MUSCLES, "Подъём рук в стороны с гантелями", "",
                "1. Встань прямо, ноги на ширине плеч, гантели в каждой руке, ладони направлены внутрь.\n\n" +
                        "2. Руки расслаблены, висят вдоль тела.\n\n" +
                        "3. Медленно поднимай руки в стороны, пока они не окажутся параллельно полу, локти слегка согнуты.\n" +
                        "4. Сделай паузу на уровне плеч и почувствуй напряжение в дельтовидных мышцах.\n\n" +
                        "5. Медленно опусти руки обратно, контролируя движение, чтобы избежать рывков.\n\n" +
                        "Важно держать плечи опущенными и не поднимать их к ушам.",
                "https://dumpster.cdn.sports.ru/a/84/c6848a151d89da18cd42dbb9720b9.jpg", "", false, 3, 6, 8, 0, 2));

        exercisesGrossMotor.add(new Exercise("14", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.DELTOID_MUSCLES, "Тяга бутылки 5 литров за ручку к подбородку", "",
                "1. Встань прямо, ноги на ширине плеч, возьми бутылку за ручку обеими руками.\n\n" +
                        "2. Держи бутылку перед собой, руки вытянуты вниз, а спина ровная.\n\n" +
                        "3. Медленно подтягивай бутылку вверх вдоль тела, локти при этом направлены в стороны.\n\n" +
                        "4. Подними бутылку до уровня подбородка, локти должны быть выше бутылки.\n\n" +
                        "5. Остановись на секунду, почувствуй напряжение в плечах и трапеции.\n\n" +
                        "6. Медленно опусти бутылку обратно в исходное положение.",
                "https://as1.ftcdn.net/v2/jpg/01/00/09/10/1000_F_100091048_hiumQCCyhviRsnS109UrJ7hsU47H0HpY.jpg", "", false, 2, 5, 8, 0, 2));

        exercisesGrossMotor.add(new Exercise("15", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.DELTOID_MUSCLES, "Жим гантелей стоя", "",
                "1. Встань прямо, ноги на ширине плеч, держи гантели в каждой руке на уровне плеч, ладони направлены вперёд.\n\n" +
                        "2. Спина ровная, взгляд прямо перед собой.\n\n" +
                        "3. Медленно поднимай гантели вверх, выпрямляя руки над головой.\n\n" +
                        "4. Сделай паузу в верхней точке, руки полностью вытянуты.\n\n" +
                        "5. Медленно опусти гантели обратно на уровень плеч.",
                "https://avatars.mds.yandex.net/i?id=6d3f9faa190c8d7018b0233190fd0b9c_l-8176266-images-thumbs&n=13", "", false, 2, 5, 7, 0, 2));

        exercisesGrossMotor.add(new Exercise("16", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.DELTOID_MUSCLES, "Развод гантелей в стороны в наклоне", "",
                "1. Встань прямо, ноги на ширине плеч, возьми гантели в обе руки.\n\n" +
                        "2. Слегка согни колени и наклонись вперёд, сохраняя спину ровной. Руки с гантелями свисают вниз.\n\n" +
                        "3. Медленно поднимай руки в стороны, пока они не окажутся параллельно полу, локти слегка согнуты.\n\n" +
                        "4. Остановись на секунду в верхней точке, почувствуй напряжение в плечах и верхней части спины.\n\n" +
                        "5. Медленно опусти руки обратно вниз.",
                "https://yandex-images.clstorage.net/5nFH4F113/765da0PBAr/qLLvzCHlA0c1qA8itqd5-RKyxsMUMayQO_guJDMd7tgBKxGYG8msXj6R8KKk_CCcusu-dk-nguBF2nVv6D4KeOrmqZtlu0RHMLxEKPq9dekH_tuADiP0nA3DUgYUIZnuKw9v4AOZ9wxt3L3-uJsdiZq9zylDx_mSG68EWedFag2Osf7nFsZKZDi6YjCyrG_lCdL4zRZwqt0W61woJwiH7Qc6YvY3mv4JTv7x7Y69GOyWl9EMb_f9rTKjLkTod286q6newTr3REsUqGIStr1uxDP20e4adKL6IcpVMg4WmdouIHTtPJ7uHWT1yv-Yli3ctZHTI3Dp2osUrg53yWFMOoaQ1MIGwkZhA7l3GrOHVtgl9cLiBUSuwDH9WT03GZf9CS9L4D-21hFh8OHEkpQGzKiq1T5C6-acAIYWfMlabhKRpMPbI_Vkbyu3YD6emnvaG_L87RNdgcAZ-UABJTWUzC0Yfu8LoMwOWODF7KSSF9yEo-wGUOPBnxmRI3LURncBppLJ-TjxT10kgkYUs7xI2QzR4-UXb7PBI-9kMxIYoOghGHXcHon6K3r99cWqlxHNqbbDKUvV1IAWixlF4ndLEp-c7uYy82FkGbFkOJ2xTus6_8TAFmSm4xPJViAePZ3kJxhI1iqyzyl57NvAlYUwzoSW_j1m9fSZH6g2Suphcg23qfT_JuZ5Uz2Ncii8gn76BOnO9Qxpodgh30Y1ETOPzxQeTMYouvgwYvLL5oiyEvGVmv0OeN74uiGNEHHhQUwajJf66hH5aUUkqXYJg4pP-zrY_OUVZ5LMNvBrPTcZntkxH0nWLrHyOGDF1fSGrRTBtrnNC27n54o6sgNB0lhsP5uS_eAez2VrAp5mCKOeZ90nxsDILUmC1THxdQsZLrz4FypCyAiX0QZSxsDSjq4sxJGvzwVo58S8E6oOUdRJVi24sOrYJcVafQiQWB2Er03zCMv61zJJkvgLz3sIEwY", "", false, 3, 4, 65, 0, 2));

        exercisesGrossMotor.add(new Exercise("17", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.PRESS, "Скручивания", "",
                "1. Ляг на спину, согни ноги в коленях, ступни поставь на пол, руки положи за голову.\n\n" +
                        "2. Напряги мышцы пресса и медленно поднимай голову и плечи от пола, скручивая корпус вперёд.\n\n" +
                        "3. Не отрывай поясницу от пола, поднимай только верхнюю часть туловища.\n\n" +
                        "4. Остановись на секунду в верхней точке, почувствуй напряжение в мышцах пресса.\n\n" +
                        "5. Медленно опустись обратно на пол.",
                "https://avatars.dzeninfra.ru/get-zen_doc/271828/pub_66ce9ec351368c7436f00d38_66cea08de993b6326db2ca4a/scale_1200", "", false, 2, 8, 5, 0, 2));

        exercisesGrossMotor.add(new Exercise("18", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.PRESS, "Подъёмы ног", "",
                "1. Ляг на спину, руки вытяни вдоль тела или положи под поясницу для поддержки.\n\n" +
                        "2. Держи ноги выпрямленными и вместе.\n\n" +
                        "3. Медленно поднимай ноги вверх, пока они не образуют угол 90 градусов с туловищем. Спина должна оставаться прижатой к полу.\n\n" +
                        "4. Сделай паузу на секунду в верхней точке.\n\n" +
                        "5. Медленно опусти ноги обратно, не касаясь пола, сохраняя контроль",
                "https://avatars.dzeninfra.ru/get-zen_doc/41204/pub_5e46b59bbb4a6d368b8d5ecd_5e46b5f211638a2a18c0b328/scale_1200", "", false, 2, 5, 6, 0, 2));

        exercisesGrossMotor.add(new Exercise("19", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.PRESS, "Ножницы", "",
                "1. Ляг на спину, руки положи под поясницу или вдоль тела для поддержки.\n\n" +
                        "2. Подними ноги немного над полом, оставляя их выпрямленными и вместе.\n\n" +
                        "3. Медленно разведите ноги в стороны, не касаясь пола, затем скрестите их, как будто делаете «ножницы».\n\n" +
                        "Повтори движение, разводя и скрещивая ноги, следя за тем, чтобы спина оставалась прижатой к полу.\n\n" +
                        "Продолжай выполнять упражнение в течение 20-30 секунд или столько, сколько сможешь.",
                "https://avatars.dzeninfra.ru/get-zen_doc/8251857/pub_64089f7e0728812eb4499f0a_6408a76a18385d22e6650aca/scale_1200", "", false, 2, 6, 7, 0, 2));

        exercisesGrossMotor.add(new Exercise("20", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.PRESS, "Пресс на мяче", "",
                "1. Сядь на фитнес-мяч, удерживая равновесие, и медленно перекатись назад, пока мяч не окажется под нижней частью спины.\n\n" +
                        "2. Убедись, что ноги стоят на полу, колени согнуты под углом 90 градусов, а стопы находятся на ширине плеч.\n" +
                        "3. Положи руки за голову или скрести на груди, сохраняя спину прямой.\n\n" +
                        "4. Напряги мышцы пресса и медленно поднимай верхнюю часть туловища, скручивая его вперёд к коленям.\n\n" +
                        "5. Остановись на секунду в верхней точке, почувствуй напряжение в мышцах живота.\n\n" +
                        "6. Медленно опустись обратно в исходное положение.",
                "https://alfagym.ru/wp-content/uploads/4/a/9/4a9d130cd6c1027724a86724b31f5948.png", "", false, 2, 6, 6, 0, 2));
        exercisesGrossMotor.add(new Exercise("21", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.UPPER_BACK_MUSCLES, "Подтягивания", "",
                "1. Найди перекладину, на которой ты сможешь удобно висеть. Хватись за перекладину ладонями вперед или назад (узкий или широкий хват).\n\n" +
                        "2. Повиси на перекладине, полностью выпрямив руки, ноги могут быть вытянуты вниз или слегка согнуты в коленях.\n\n" +
                        "3. Напряги мышцы спины и рук и медленно подтягивай тело вверх, стараясь дотянуться подбородком до перекладины.\n\n" +
                        "4. Остановись на секунду в верхней точке, почувствуй напряжение в мышцах спины и рук.\n\n" +
                        "5. Медленно опусти тело обратно в исходное положение, полностью выпрямляя руки.\n\n" +
                        "Подстройте это упражнение под возможности ребенка.\n\n" +
                        "Если подтягивания даются тяжело, можно использовать резиновую петлю для поддержки или выполнять отрицательные подтягивания, опускаясь медленно вниз.\n\n\n" +
                        "Отрицательные подтягивания — это упражнение, помогающее развить силу для выполнения подтягиваний.\n\n" +
                        "Как выполнять:\n\n" +
                        "1. Возьми перекладину хватом, подними подбородок выше неё (можно использовать стул).\n\n" +
                        "2. Медленно опускай тело вниз, контролируя движение до полного выпрямления рук.\n\n" +
                        "3. Снова поднимись с помощью стула и повтори.",
                "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/u05-bottomhalfwaytop-ism-mh310118-1558552383.jpg", "", false, 2, 3, 8, 0, 3));

        exercisesGrossMotor.add(new Exercise("22", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.UPPER_BACK_MUSCLES, "Подтягивания на низкой перекладине", "",
                "1. Найди низкую перекладину или стойку, чтобы хвататься за неё, лежа на земле.\n\n" +
                        "2. Ляг на спину под перекладиной, потянись вверх и схвати перекладину хватом на ширине плеч.\n\n" +
                        "3. Держи ноги на полу или поднимай их немного, если нужно.\n\n" +
                        "4. Напряги мышцы спины и рук и подтягивай тело к перекладине, пока грудь не окажется рядом с ней.\n\n" +
                        "5. Остановись на секунду в верхней точке, почувствуй напряжение.\n\n" +
                        "6. Медленно опусти тело обратно в исходное положение.\n\n" +
                        "Подстройте это упражнение под возможности ребенка.\n\n" +
                        "Подтягивания на низкой перекладине являются отличной альтернативой, если традиционные подтягивания пока не доступны.",
                "https://sport96.ru/uploadedFiles/eshopimages/big/262f71b690fa1fa06b6acea945d00e84_3.jpeg", "", false, 3, 4, 7, 0, 2));

        exercisesGrossMotor.add(new Exercise("23", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.UPPER_BACK_MUSCLES, "Тяга штанги к груди в наклоне", "",
                "1. Встань прямо, ноги на ширине плеч, слегка согни колени.\n" +
                        "2. Наклонись вперёд, сохраняя спину ровной и таз немного отведённым назад.\n\n" +
                        "3. Возьми штангу хватом чуть шире плеч, руки полностью вытянуты вниз.\n\n" +
                        "4. Напряги мышцы спины и подтягивай штангу к груди, локти должны быть направлены в стороны.\n\n" +
                        "5. Остановись на секунду, почувствуй напряжение в спине и руках.\n\n" +
                        "6. Медленно опусти штангу обратно в исходное положение.",
                "https://avatars.dzeninfra.ru/get-zen_doc/203431/pub_5b2f25d75902d200a909510a_5b2f294142f09800a89f886e/scale_1200", "", false, 2, 4, 7, 0, 2));

        // todo
        exercisesGrossMotor.add(new Exercise("24", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.UPPER_BACK_MUSCLES, "Упражнение с резинкой", "",
                "1. Встань прямо, возьми резинку в обе руки.\n\n" +
                        "2. Слегка согни колени и наклонись вперёд, сохраняя спину ровной.\n\n" +
                        "3. Растягивай резинку, подтягивая руки к груди, сводя локти назад и в стороны, пока не почувствуешь напряжение в спине.\n\n" +
                        "4. Верни руки в исходное положение, контролируя движение.",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", "", false, 3, 8, 3, 0, 2));

        exercisesGrossMotor.add(new Exercise("25", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.QUADRICEPS, "Приседания с грифом", "",
                "1. Встань прямо, ноги на ширине плеч, гриф положи на плечи за головой, удерживая его руками.\n\n" +
                        "2. Слегка прогни поясницу, плечи отведи назад, грудь вперёд.\n\n" +
                        "3. Медленно опускайся в присед, сгибая колени и отводя таз назад, пока бедра не будут параллельны полу.\n\n" +
                        "4. Остановись на секунду в нижней точке, сохраняя спину прямой.\n\n" +
                        "5. Напряги мышцы и поднимайся обратно в исходное положение.",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", "", false, 2, 5, 6, 0, 2));

        exercisesGrossMotor.add(new Exercise("26", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.QUADRICEPS, "Выпады с гантелями в руках", "",
                "1. Встань прямо, ноги на ширине плеч, в каждой руке держи гантели вдоль тела.\n\n" +
                        "2. Сделай широкий шаг вперёд одной ногой, опускаясь в выпаде, пока оба колена не согнутся под углом 90 градусов. Заднее колено должно почти касаться пола.\n\n" +
                        "3. Держи спину ровной и корпус вертикально.\n\n" +
                        "4. Напряги мышцы квадрицепсов и поднимись обратно, возвращаясь в исходное положение.\n\n" +
                        "5. Повтори движение на другую ногу.",
                "https://avatars.mds.yandex.net/i?id=e8f08e624ecf85b4a9574b2e9cc9f646_l-5236662-images-thumbs&n=13", "", false, 3, 5, 6, 0, 2));

        exercisesGrossMotor.add(new Exercise("27", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.QUADRICEPS, "Выпады с лавки", "",
                "1. Встань спиной к лавке, одну ногу поставь на лавку, передняя часть стопы опирается на край.\n\n" +
                        "2. Держи руки на поясе или вдоль тела с гантелями, если используешь вес.\n\n" +
                        "3. Согни переднее колено и медленно опускайся вниз, пока бедро передней ноги не станет параллельно полу. Заднее колено при этом опускается к полу.\n\n" +
                        "4. Держи спину ровной и корпус вертикально.\n\n" +
                        "5. Напряги мышцы квадрицепсов и поднимись обратно в исходное положение.\n\n" +
                        "6. Повтори для другой ноги.",
                "https://i.pinimg.com/736x/ce/50/51/ce50519da3e604082faef325ae88d25f.jpg", "", false, 3, 3, 7, 0, 2));

        exercisesGrossMotor.add(new Exercise("28", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.QUADRICEPS, "Разгибание ног с грузом", "",
                "1. Сядь на скамью или стул, спина прижата к спинке, ноги согнуты в коленях.\n\n" +
                        "2. Закрепи груз или специальный тренажёр на лодыжках.\n\n" +
                        "3. Медленно выпрямляй ноги, поднимая их перед собой, пока они не окажутся параллельно полу.\n\n" +
                        "4. Задержись на секунду в верхней точке, почувствуй напряжение в квадрицепсах.\n\n" +
                        "5. Медленно опусти ноги обратно в исходное положение.",
                "https://i.ytimg.com/vi/40b3VQRPlBU/maxresdefault.jpg", "", false, 2, 6, 5, 0, 2));

        exercisesGrossMotor.add(new Exercise("29", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.LOWER_BACK_MUSCLES, "Гиперэкстензия", "",
                "1. Ляг лицом вниз на скамью или специальный тренажёр для гиперэкстензий, так чтобы верхняя часть бедер находилась на краю платформы, а стопы были закреплены под валиками.\n\n" +
                        "2. Скрести руки на груди или положи их за голову.\n\n" +
                        "3. Медленно наклоняйся вперёд, опуская корпус вниз, сгибаясь в талии, пока не почувствуешь легкое растяжение в мышцах спины.\n\n" +
                        "4. Поднимайся обратно, напрягая мышцы нижней части спины, пока корпус не станет в одну линию с ногами. Не прогибайся слишком сильно назад.\n\n" +
                        "5. Сделай паузу на секунду в верхней точке, затем медленно вернись в исходное положение.",
                "https://avatars.mds.yandex.net/i?id=4e0d71979625183de90db5ad691b12842ad134ff-6977815-images-thumbs&n=13", "", false, 3, 4, 8, 0, 2));

        exercisesGrossMotor.add(new Exercise("30", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.LOWER_BACK_MUSCLES, "Обратная гиперэкстензия", "",
                "1. Ляг на скамью или специальный тренажёр для гиперэкстензий, так чтобы верхняя часть тела была на скамье, а ноги свисали вниз. Держись руками за край скамьи для устойчивости.\n\n" +
                        "2. Оставь ноги прямыми и слегка сведёнными вместе.\n\n" +
                        "3. Напряги мышцы нижней части спины и ягодиц, медленно поднимай ноги вверх, пока они не станут параллельны полу или немного выше.\n\n" +
                        "4. Задержись в верхней точке на секунду, почувствуй напряжение в мышцах спины и ягодиц.\n\n" +
                        "5. Медленно опусти ноги обратно в исходное положение, контролируя движение.",
                "https://avatars.mds.yandex.net/i?id=c10de5225caa2b46521c8365d504caa186ad11a5-5499277-images-thumbs&n=13", "", false, 3, 4, 8, 0, 2));

        exercisesGrossMotor.add(new Exercise("31", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.LOWER_BACK_MUSCLES, "Подъёмы рук и ног из положения лёжа на животе", "",
                "1. Ляг на живот, вытяни руки вперед, ноги прямые, лоб можно опустить на пол.\n\n" +
                        "2. Одновременно поднимай руки, грудь и ноги как можно выше от пола, напрягая мышцы спины и ягодиц.\n\n" +
                        "3. Задержись в верхней точке на секунду, почувствуй напряжение в мышцах спины, ягодиц и задней поверхности бедра.\n\n" +
                        "4. Медленно опусти руки и ноги обратно в исходное положение, расслабив мышцы.",
                "https://avatars.mds.yandex.net/i?id=9a1a66d6e196518acb7fa46b21f3d63b45b2bab6-4379030-images-thumbs&n=13", "", false, 2, 4, 7, 0, 2));

        exercisesGrossMotor.add(new Exercise("32", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.LOWER_BACK_MUSCLES, "Становая тяга штанги", "",
                "1. Встань прямо, ноги на ширине плеч, штанга лежит на полу перед тобой.\n\n" +
                        "2. Наклонись вперёд, сгибая колени и держа спину ровной. Возьми штангу хватом чуть шире плеч.\n\n" +
                        "3. Напряги мышцы спины, ягодиц и ног. Поднимай штангу, выпрямляя спину и ноги одновременно, пока не встанешь прямо.\n\n" +
                        "4. Держи штангу близко к телу, не прогибайся в пояснице.\n\n" +
                        "5. Медленно опусти штангу обратно на пол, сгибая колени и наклоняя корпус вперед, сохраняя спину ровной.",
                "https://i.pinimg.com/originals/61/30/04/61300442cf4225ba94b18b4fa0064137.jpg", "", false, 2, 7, 7, 0, 2));

        exercisesGrossMotor.add(new Exercise("33", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.FULL_BODY, "Бег", "",
                "1. Начни с лёгкого бега трусцой.\n\n" +
                        "2. Постепенно увеличивай темп в зависимости от уровня физической подготовки.\n\n" +
                        "Если устанешь, можно чередовать бег с ходьбой.",
                "https://avatars.mds.yandex.net/i?id=a227635f676efb47e3752aa9d1c03715dde2e0a8-7664914-images-thumbs&n=13", "минут", false, 1, 10, 8, 0, 2));

        exercisesGrossMotor.add(new Exercise("34", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.FULL_BODY, "Приседания", "",
                "1. Встань прямо, ноги на ширине плеч.\n\n" +
                        "2. Медленно сгибай колени, опускаясь в присед, пока бедра не будут параллельны полу.\n\n" +
                        "3. Держи спину ровной и руки вытянутыми перед собой для баланса.\n\n" +
                        "4. Вернись в исходное положение, выпрямляя ноги.",
                "https://avatars.mds.yandex.net/i?id=d41f40abd44e784be53ecaaf461cbdd9_l-5276035-images-thumbs&n=13", "", false, 3, 6, 7, 0, 2));

        exercisesGrossMotor.add(new Exercise("35", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.FULL_BODY, "Отжимания", "",
                "1. Ляг на живот, руки поставь немного шире плеч, ладони должны быть направлены вниз.\n\n" +
                        "2. Ноги держи вместе, упираясь пальцами в пол.\n\n" +
                        "3. Напрягай мышцы корпуса и рук, медленно поднимай тело, выпрямляя руки.\n\n" +
                        "4. Держи спину ровной, не прогибай поясницу и не поднимай таз слишком высоко.\n\n" +
                        "5. Опусти тело вниз, сгибая локти, пока грудь почти не коснется пола.\n\n" +
                        "6. Затем снова поднимайся в исходное положение.",
                "https://avatars.dzeninfra.ru/get-zen_doc/1708669/pub_5dcac2e5469b8d2c58ff2502_5dcad81093b4f037b147f057/scale_1200", "", false, 2, 4, 8, 0, 2));

        exercisesGrossMotor.add(new Exercise("36", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.FULL_BODY, "Планка", "",
                "1. Ляг на живот, затем поднимись на локтях и носках.\n" +
                        "2. Держи локти под плечами, а тело в одной линии от головы до пяток.\n\n" +
                        "3. Напрягай мышцы живота, ягодиц и спины, чтобы не прогибаться в пояснице.\n\n" +
                        "4. Сохраняй положение планки, удерживая дыхание ровным.\n\n" +
                        "5. Держись в этом положении 30 секунд или столько, сколько сможешь, затем медленно опустись на пол.",
                "https://avatars.mds.yandex.net/i?id=6c519f7f6a3c045bd8d997748e0d6cc881505b50-10330387-images-thumbs&n=13", "секунд", false, 1, 30, 8, 0, 2));
        //todo поменять картинку
        exercisesGrossMotor.add(new Exercise("37", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.FULL_BODY, "Ходьба на руках", "",
                "1. Ребёнок принимает положение для ходьбы на руках: руки на полу, ноги подняты.\n" +
                        "2. Родитель аккуратно берёт ребёнка за ноги, держа их на уровне своего пояса.\n" +
                        "3. Ребёнок начинает двигаться вперёд, используя только руки для передвижения.\n" +
                        "4. Родитель идёт за ребёнком, удерживая ноги и помогая сохранять баланс.\n",
                "https://fhd.multiurok.ru/f/c/4/fc42a6c84d76b0db314b3e747470f6bdeb1c9437/volieibol-obuchieniie-tiekhnikie-mietodikie-ighry_2.jpeg", "секунд", false, 2, 15, 8, 0, 2));

        exercisesGrossMotor.add(new Exercise("38", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.FULL_BODY, "Прогулка фермера", "",
                "1. Встань прямо, в каждой руке держи гантели.\n\n" +
                        "2. Ноги на ширине плеч, спина прямая, плечи расслаблены.\n\n" +
                        "3. Начни медленно идти вперёд, сохраняя прямую спину и сильный корпус.\n\n" +
                        "4. Не торопись, делай шаги комфортного для себя размера.\n\n" +
                        "5. Следи за тем, чтобы руки не поднимались вверх, а оставались вдоль тела.\n\n" +
                        "Продолжай идти в течение 30 секунд или столько, сколько сможешь, затем сделай паузу и отдохни.",
                "https://avatars.mds.yandex.net/i?id=6435cb5e96101774f3b391b70c96a40d633d951cc97a5647-12510741-images-thumbs&n=13", "секунд", false, 2, 30, 5, 0, 2));

        //todo поменять картинку
        exercisesGrossMotor.add(new Exercise("39", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.FULL_BODY, "Ставим-снимаем (упражнение с грузом)", "",
                "1. Положи груз на пол.\n\n" +
                        "2. Поднимай его вверх, а затем снова ставь на пол.\n\n",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", "", false, 3, 5, 7, 0, 2));

        //todo поменять картинку
        exercisesGrossMotor.add(new Exercise("40", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.FULL_BODY, "Тяни-толкай", "",
                "1. Встань прямо, ноги на ширине плеч. Закрепи резинку или верёвку на уровне груди.\n\n" +
                        "2. Возьмись за резинку обеими руками, руки вытянуты перед собой. Потяни резинку к себе, сгибая локти и сводя лопатки.\n\n" +
                        "3. Задержитесь на секунду в верхней точке.\n\n" +
                        "4. Верни руки в исходное положение, выпрямляя их и толкая резинку обратно. При этом активируй грудные мышцы.\n\n" +
                        "5. Чередуй тягу и толчок, контролируя движение.",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", "", false, 2, 6, 6, 0, 2));

        exercisesGrossMotor.add(new Exercise("41", MotorSkillGroup.GROSS_MOTOR, GrossMotorMuscleGroup.FULL_BODY, "Тяга груза за верёвку сидя", "",
                "1. Сядь на пол, выпрями ноги перед собой.\n" +
                        "2. Закрепи верёвку или канат на тренажёре или другом объекте.\n" +
                        "3. Возьмись за верёвку обеими руками, руки вытянуты перед собой.\n" +
                        "4. Напряги мышцы спины и потяни верёвку к себе, сгибая локти и сводя лопатки.\n" +
                        "5. Задержись на секунду в верхней точке, чувствуя напряжение в мышцах спины.\n" +
                        "6. Медленно вернись в исходное положение, контролируя движение.",
                "https://avatars.mds.yandex.net/i?id=2dae00e2c15a110a741a5e9f8e13da80ba33db33-4843912-images-thumbs&n=13", "", false, 2, 6, 7, 0, 2));

//        exercisesFineMotor.add(new Exercise("47", MotorSkillGroup.FINE_MOTOR, FineMotorMuscleGroup.HAND_CONTROL, "Сортировка мелких предметов",
//                "", ))

//        exercises.add(new Exercise("42", Exercise.ExerciseType.FAMILY_COMPETITION, "Синхронные приседания с мячом",
//                "1. Родитель и ребенок встают друг напротив друга, держа между собой мяч (например, фитбол или обычный надувной мяч).\n\n" +
//                        "2. Они должны выполнять приседания одновременно, не уронив мяч.\n\n" +
//                        "Цель: Развитие координации, силы ног и командной работы.",
//                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 3, 1, "минуте", 75, 0, 2));
//
//        exercises.add(new Exercise("43", Exercise.ExerciseType.FAMILY_COMPETITION, "Бег на тройных ногах",
//                "1. Свяжите одну ногу родителя и одну ногу ребенка вместе.\n" +
//                        "2. Родитель и ребенок должны пройти (или пробежать) определенное расстояние, не упав и не развязав ноги.\n\n" +
//                        "Цель: Развитие координации и взаимодействия.",
//                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 2, 2, "минуты", 80, 0, 2));
//
//        exercises.add(new Exercise("44", Exercise.ExerciseType.FAMILY_COMPETITION, "Перенос предметов на голове",
//                "1. Родитель и ребенок должны поочередно переносить легкие предметы (например, небольшие мягкие игрушки или подушки) на голове.\n\n" +
//                        "2. Нужно пройти определенное расстояние и передать предмет следующему члену команды (например, друг другу).\n\n" +
//                        "Цель: Развитие баланса, координации и внимательности.",
//                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 3, 6, "", 70, 0, 2));
//
//        exercises.add(new Exercise("45", Exercise.ExerciseType.FAMILY_COMPETITION, "Упражнение на точность: «Кольца на кегли»",
//                "1. Родитель и ребенок по очереди бросают пластиковые кольца или обручи на кегли или конусы.\n\n" +
//                        "Цель: Развитие координации глаз и рук, а также концентрации.",
//                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 3, 8, "", 50, 0, 2));
//
//        exercises.add(new Exercise("46", Exercise.ExerciseType.FAMILY_COMPETITION, "Удержание баланса на одной ноге",
//                "1. Родитель и ребенок становятся друг напротив друга и должны стоять на одной ноге как можно дольше.\n\n" +
//                        "Цель: Развитие равновесия и концентрации.",
//                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 3, 25, "секунд", 70, 0, 2));


        // Проверяем, существуют ли упражнения
        getAllExercises(existingExercises -> {
            // Создаем список существующих названий упражнений
            List<String> existingNames = existingExercises.stream()
                    .map(Exercise::getName)
                    .collect(Collectors.toList());

            // Перебираем каждое упражнение из списка
            for (Exercise exercise : exercisesGrossMotor) {
                // Если названия нет в списке существующих, добавляем его
                if (!existingNames.contains(exercise.getName())) {
                    exerciseRepository.add(exercise, onSuccess, onFailure);
                } else {
                    System.out.println("Упражнение \"" + exercise.getName() + "\" уже существует. Пропускаем.");
                }
            }
        }, onFailure);
    }

    public void initializeDayPlans(OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        // Создаем список упражнений для дня
        List<Exercise> mondayExercisesGrossMotor = new ArrayList<>();
        List<Exercise> tuesdayExercisesGrossMotor = new ArrayList<>();
        List<Exercise> wednesdayExercisesGrossMotor = new ArrayList<>();
        List<Exercise> thursdayExercisesGrossMotor = new ArrayList<>();
        List<Exercise> fridayExercisesGrossMotor = new ArrayList<>();
//        List<Exercise> saturdayExercises = new ArrayList<>();

        List<Exercise> mondayExercisesFineMotor = new ArrayList<>();
        List<Exercise> tuesdayExercisesFineMotor = new ArrayList<>();
        List<Exercise> wednesdayExercisesFineMotor = new ArrayList<>();
        List<Exercise> thursdayExercisesFineMotor = new ArrayList<>();
        List<Exercise> fridayExercisesFineMotor = new ArrayList<>();

        // Добавляем необходимые упражнения из уже созданного списка
        for (Exercise exercise : exercisesGrossMotor) {
            if (exercise.getMuscleGroup() == GrossMotorMuscleGroup.BICEPS || exercise.getMuscleGroup() == GrossMotorMuscleGroup.PECTORAL_MUSCLES) {
                mondayExercisesGrossMotor.add(exercise);
            }
            if (exercise.getMuscleGroup() == GrossMotorMuscleGroup.TRICEPS || exercise.getMuscleGroup() == GrossMotorMuscleGroup.DELTOID_MUSCLES) {
                tuesdayExercisesGrossMotor.add(exercise);
            }
            if(exercise.getMuscleGroup() == GrossMotorMuscleGroup.PRESS || exercise.getMuscleGroup() == GrossMotorMuscleGroup.UPPER_BACK_MUSCLES) {
                wednesdayExercisesGrossMotor.add(exercise);
            }
            if(exercise.getMuscleGroup() == GrossMotorMuscleGroup.QUADRICEPS || exercise.getMuscleGroup() == GrossMotorMuscleGroup.LOWER_BACK_MUSCLES) {
                thursdayExercisesGrossMotor.add(exercise);
            }
            if(exercise.getMuscleGroup() == GrossMotorMuscleGroup.FULL_BODY) {
                fridayExercisesGrossMotor.add(exercise);
            }
        }

        // Создаем список дневных планов
        List<DayPlan> dayPlans = new ArrayList<>();
        dayPlans.add(new DayPlan("monday", WeekDay.MONDAY, mondayExercisesGrossMotor, mondayExercisesFineMotor, false, 800));
        dayPlans.add(new DayPlan("tuesday", WeekDay.TUESDAY, tuesdayExercisesGrossMotor, tuesdayExercisesFineMotor, false, 800));
        dayPlans.add(new DayPlan("wednesday", WeekDay.WEDNESDAY, wednesdayExercisesGrossMotor, wednesdayExercisesFineMotor, false, 800));
        dayPlans.add(new DayPlan("thursday", WeekDay.THURSDAY, thursdayExercisesGrossMotor, thursdayExercisesFineMotor, false, 800));
        dayPlans.add(new DayPlan("friday", WeekDay.FRIDAY, fridayExercisesGrossMotor, fridayExercisesFineMotor, false, 800));
//        dayPlans.add(new DayPlan("saturday", WeekDay.SATURDAY, saturdayExercises, false));

        // Проверка, на существование дневных планов
        getAllDayPlans(existingDayPlans -> {
            // Создаем список существующих дней недели
            List<String> existingDays = existingDayPlans.stream()
                    .map(dayPlan -> dayPlan.getWeekDay().name())
                    .collect(Collectors.toList());

            // Перебор каждого плана дня из списка
            for (DayPlan dayPlan : dayPlans) {
                // Если дня недели нет в списке существующих, он добавится
                if (!existingDays.contains(dayPlan.getWeekDay().name())) {
                    dayPlanRepository.add(dayPlan, onSuccess, onFailure);
                } else {
                    System.out.println("Дневной план на \"" + dayPlan.getWeekDay().name() + "\" уже существует. Пропускаем.");
                }
            }
        }, onFailure);

    }

    // Метод для получения всех дневных планов из Firestore
    public void getAllDayPlans(OnSuccessListener<List<DayPlan>> onSuccess, OnFailureListener onFailure) {
        dayPlanRepository.getAll(onSuccess, onFailure);
    }

//    public void initializeTrainingPlans(OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
//        // Создаем список тренировочных планов
//        List<TrainingPlan> trainingPlans = new ArrayList<>();
//
//        // Пример создания тренировочного плана с уникальным id и присоединенными дневными планами
//        List<DayPlan> days = new ArrayList<>();
//        days.add(new DayPlan(UUID.randomUUID().toString(), DayPlan.WeekDay.MONDAY, new ArrayList<>()));
//        days.add(new DayPlan(UUID.randomUUID().toString(), DayPlan.WeekDay.TUESDAY, new ArrayList<>()));
//
//        TrainingPlan trainingPlan = new TrainingPlan(UUID.randomUUID().toString(), "My First Training Plan", days);
//        trainingPlans.add(trainingPlan);
//
//        // Проверяем, существуют ли тренировочные планы
//        getAllTrainingPlans(existingTrainingPlans -> {
//            // Создаем список существующих названий тренировочных планов
//            List<String> existingNames = existingTrainingPlans.stream()
//                    .map(TrainingPlan::getTitle)  // Проверяем по имени тренировочного плана
//                    .collect(Collectors.toList());
//
//            // Перебираем каждый тренировочный план из списка
//            for (TrainingPlan plan : trainingPlans) {
//                // Если имени нет в списке существующих, добавляем его
//                if (!existingNames.contains(plan.getTitle())) {
//                    trainingPlanRepository.add(plan, onSuccess, onFailure);
//                } else {
//                    System.out.println("Тренировочный план \"" + plan.getTitle() + "\" уже существует. Пропускаем.");
//                }
//            }
//        }, onFailure);
//    }

//    // Метод для получения всех тренировочных планов из Firestore
//    private void getAllTrainingPlans(OnSuccessListener<List<TrainingPlan>> onSuccess, OnFailureListener onFailure) {
//        trainingPlanRepository.getAll(onSuccess, onFailure);
//    }

    // Метод для получения всех упражнений из Firestore
    private void getAllExercises(OnSuccessListener<List<Exercise>> onSuccess, OnFailureListener onFailure) {
        exerciseRepository.getAll(onSuccess, onFailure);
    }
}
