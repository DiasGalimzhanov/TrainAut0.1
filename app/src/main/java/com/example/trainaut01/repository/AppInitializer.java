package com.example.trainaut01.repository;

import com.example.trainaut01.models.DayPlan;
import com.example.trainaut01.models.Exercise;
import com.example.trainaut01.models.TrainingPlan;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class AppInitializer {
    private final ExerciseRepository exerciseRepository;
    private final DayPlanRepository dayPlanRepository;
//    private final TrainingPlanRepository trainingPlanRepository;

    private List<Exercise> exercises;

    @Inject
    public AppInitializer(ExerciseRepository exerciseRepository, DayPlanRepository dayPlanRepository) {
        this.exerciseRepository = exerciseRepository;
        this.dayPlanRepository = dayPlanRepository;
//        this.trainingPlanRepository = trainingPlanRepository;
        this.exercises = new ArrayList<>();
    }

    public void initializeExercises(OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {

        exercises.add(new Exercise("1", Exercise.ExerciseType.BICEPS,"Сгибание рук стоя с гантелями",
                "1. Возьми в каждую руку по гантели, ладони направлены вперед. \n" +
                        "2. Встань прямо, ноги на ширине плеч, спина ровная. \n" +
                        "3. Медленно сгибай руки в локтях, поднимая гантели к плечам. \n" +
                        "4. Когда гантели будут у плеч, остановись на секунду. \n" +
                        "5. Затем медленно опусти гантели обратно, выпрямляя руки. \n" +
                        "Не спеши, важно выполнять упражнение медленно и аккуратно.",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 2, 10, 2));

        exercises.add(new Exercise("2", Exercise.ExerciseType.BICEPS, "Сгибание рук, изолированное сидя с упором в бедро",
                        "1. Сядь на стул или скамейку, поставь ноги широко. \n" +
                                "2. Возьми гантель в одну руку, а локоть этой руки поставь на внутреннюю часть бедра, чтобы рука была уперта. \n" +
                                "3. Другая рука может лежать на другом бедре для баланса. \n" +
                                "4. Медленно поднимай гантель, сгибая руку в локте, пока гантель не будет близко к плечу. \n" +
                                "5. Остановись на секунду, затем медленно опусти гантель обратно, выпрямляя руку. \n" +
                                "Выполняй упражнение медленно и следи за тем, чтобы не напрягать спину.",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 3, 8, 1.5f));

        exercises.add(new Exercise("3", Exercise.ExerciseType.BICEPS, "Сгибание рук стоя со штангой",
                "1. Возьми штангу двумя руками, ладони направлены вперед, руки на ширине плеч. \n" +
                        "2. Встань прямо, ноги на ширине плеч, спина ровная. \n" +
                        "3. Медленно сгибай руки в локтях, поднимая штангу к груди. \n" +
                        "4. Когда штанга будет у груди, остановись на секунду. \n" +
                        "5. Затем медленно опусти штангу обратно, выпрямляя руки. \n" +
                        "Не спеши, важно выполнять упражнение медленно, чтобы не напрягать спину.",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 2, 10, 2));

        exercises.add(new Exercise("4", Exercise.ExerciseType.BICEPS, "Сгибание рук стоя в стороны с гантелями",
                "1. Возьми в каждую руку по гантели, ладони направлены к телу. \n" +
                        "2. Встань прямо, ноги на ширине плеч, спина ровная. \n" +
                        "3. Начни с того, что руки опущены вниз по бокам тела. \n" +
                        "4. Медленно поднимай обе руки в стороны, пока гантели не окажутся на уровне плеч. \n" +
                        "5. Остановись на секунду, чтобы почувствовать напряжение в бицепсах. \n" +
                        "6. Затем медленно опусти руки обратно в исходное положение. \n" +
                        "Не спеши, следи за тем, чтобы спина оставалась прямой и плечи не поднимались вверх.",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 3, 10, 2));

        exercises.add(new Exercise("5", Exercise.ExerciseType.PECTORAL_MUSCLES, "Жим штанги лёжа",
                "1. Ляг на скамью, держи штангу над собой.\n" +
                        "2. Опусти штангу к груди, сгибая локти.\n" +
                        "3. Поднимай штангу обратно вверх.\n",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 1, 10, 2));

        exercises.add(new Exercise("6", Exercise.ExerciseType.PECTORAL_MUSCLES, "Развод гантелей на скамье",
                "1. Ляг на скамью, держи гантели над собой.\n" +
                        "2. Медленно разводи руки в стороны, не сгибая локти.\n" +
                        "3. Верни руки обратно в исходное положение.\n",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 2, 10, 2));

        exercises.add(new Exercise("7", Exercise.ExerciseType.PECTORAL_MUSCLES, "Жим гантелей лёжа",
                "1. Ляг на скамью, держи гантели над грудью.\n" +
                        "2. Опусти гантели к груди, сгибая локти.\n" +
                        "3. Поднимай гантели обратно вверх.",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 3, 10, 2));

        exercises.add(new Exercise("8", Exercise.ExerciseType.PECTORAL_MUSCLES, "Отжимания широким хватом",
                "1. Встань в упор лежа, руки шире плеч.\n" +
                        "2. Опусти тело вниз, сгибая локти.\n" +
                        "3. Поднимай тело обратно вверх.",
                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 2, 5, 2));

//        exercises.add(new Exercise("9", Exercise.ExerciseType.TRICEPS, "отжимания от лавки на трицепс",
//                "1. Возьми в каждую руку по гантели, ладони направлены к телу. " +
//                        "2. Встань прямо, ноги на ширине плеч, спина ровная. " +
//                        "3. Начни с того, что руки опущены вниз по бокам тела. " +
//                        "4. Медленно поднимай обе руки в стороны, пока гантели не окажутся на уровне плеч. " +
//                        "5. Остановись на секунду, чтобы почувствовать напряжение в бицепсах. " +
//                        "6. Затем медленно опусти руки обратно в исходное положение. " +
//                        "Повтори это движение 10 раз. " +
//                        "Не спеши, следи за тем, чтобы спина оставалась прямой и плечи не поднимались вверх.",
//                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 2, 10, 2));
//
//        exercises.add(new Exercise("10", Exercise.ExerciseType.TRICEPS, "французский жим гантелей лёжа",
//                "1. Возьми в каждую руку по гантели, ладони направлены к телу. " +
//                        "2. Встань прямо, ноги на ширине плеч, спина ровная. " +
//                        "3. Начни с того, что руки опущены вниз по бокам тела. " +
//                        "4. Медленно поднимай обе руки в стороны, пока гантели не окажутся на уровне плеч. " +
//                        "5. Остановись на секунду, чтобы почувствовать напряжение в бицепсах. " +
//                        "6. Затем медленно опусти руки обратно в исходное положение. " +
//                        "Повтори это движение 10 раз. " +
//                        "Не спеши, следи за тем, чтобы спина оставалась прямой и плечи не поднимались вверх.",
//                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 2, 10, 2));
//
//        exercises.add(new Exercise("10", Exercise.ExerciseType.TRICEPS, "жим гантелей из-за головы сидя",
//                "1. Возьми в каждую руку по гантели, ладони направлены к телу. " +
//                        "2. Встань прямо, ноги на ширине плеч, спина ровная. " +
//                        "3. Начни с того, что руки опущены вниз по бокам тела. " +
//                        "4. Медленно поднимай обе руки в стороны, пока гантели не окажутся на уровне плеч. " +
//                        "5. Остановись на секунду, чтобы почувствовать напряжение в бицепсах. " +
//                        "6. Затем медленно опусти руки обратно в исходное положение. " +
//                        "Повтори это движение 10 раз. " +
//                        "Не спеши, следи за тем, чтобы спина оставалась прямой и плечи не поднимались вверх.",
//                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 2, 10, 2));
//
//        exercises.add(new Exercise("10", Exercise.ExerciseType.TRICEPS, "жим гантелей с переносом центра тяжести на лавку",
//                "1. Возьми в каждую руку по гантели, ладони направлены к телу. " +
//                        "2. Встань прямо, ноги на ширине плеч, спина ровная. " +
//                        "3. Начни с того, что руки опущены вниз по бокам тела. " +
//                        "4. Медленно поднимай обе руки в стороны, пока гантели не окажутся на уровне плеч. " +
//                        "5. Остановись на секунду, чтобы почувствовать напряжение в бицепсах. " +
//                        "6. Затем медленно опусти руки обратно в исходное положение. " +
//                        "Повтори это движение 10 раз. " +
//                        "Не спеши, следи за тем, чтобы спина оставалась прямой и плечи не поднимались вверх.",
//                "https://cdn.culture.ru/images/593bd4d7-de4f-509d-9895-d506ad59ddb8", false, 2, 10, 2));


        // Проверяем, существуют ли упражнения
        getAllExercises(existingExercises -> {
            // Создаем список существующих названий упражнений
            List<String> existingNames = existingExercises.stream()
                    .map(Exercise::getName)
                    .collect(Collectors.toList());

            // Перебираем каждое упражнение из списка
            for (Exercise exercise : exercises) {
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
        List<Exercise> mondayExercises = new ArrayList<>();

        // Добавляем необходимые упражнения из уже созданного списка
        for (Exercise exercise : exercises) {
            if (exercise.getType() == Exercise.ExerciseType.BICEPS || exercise.getType() == Exercise.ExerciseType.PECTORAL_MUSCLES) {
                mondayExercises.add(exercise);
            }
        }

        // Создаем список дневных планов
        List<DayPlan> dayPlans = new ArrayList<>();
        dayPlans.add(new DayPlan("monday", DayPlan.WeekDay.MONDAY, mondayExercises));
        // Добавьте другие дни по необходимости с их соответствующими упражнениями

        // Проверяем, существуют ли дневные планы
        getAllDayPlans(existingDayPlans -> {
            // Создаем список существующих дней недели
            List<String> existingDays = existingDayPlans.stream()
                    .map(dayPlan -> dayPlan.getWeekDay().name())
                    .collect(Collectors.toList());

            // Перебираем каждый план дня из списка
            for (DayPlan dayPlan : dayPlans) {
                // Если дня недели нет в списке существующих, добавляем его
                if (!existingDays.contains(dayPlan.getWeekDay().name())) {
                    dayPlanRepository.add(dayPlan, onSuccess, onFailure);
                } else {
                    System.out.println("Дневной план на \"" + dayPlan.getWeekDay().name() + "\" уже существует. Пропускаем.");
                }
            }
        }, onFailure);
    }

    // Метод для получения всех дневных планов из Firestore
    private void getAllDayPlans(OnSuccessListener<List<DayPlan>> onSuccess, OnFailureListener onFailure) {
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
