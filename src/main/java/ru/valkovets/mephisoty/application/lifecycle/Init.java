package ru.valkovets.mephisoty.application.lifecycle;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Configuration;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.repository.season.SeasonRepository;
import ru.valkovets.mephisoty.db.repository.season.StageRepository;
import ru.valkovets.mephisoty.settings.AllowState;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class Init implements SmartInitializingSingleton {
public static final Long _2024_SEASON_ID = 1L;
public static final Long _2024_SEASON_FINAL_ID = 2L;

public static final Long _2024_MATHS_STAGE_ID = 1L;
public static final Long _2024_HACKATHON_STAGE_ID = 2L;
public static final Long _2024_WWW_STAGE_ID = 3L;
public static final Long _2024_WIREPARK_STAGE_ID = 4L;
public static final Long _2024_VIDEO_STAGE_ID = 6L;
public static final Long _2024_DICTANT_STAGE_ID = 7L;
public static final Long _2024_PORTFOLIO_STAGE_ID = 10L;

public static final List<Long> _STAGES_ID = List.of(
    _2024_MATHS_STAGE_ID,
    _2024_HACKATHON_STAGE_ID,
    _2024_WWW_STAGE_ID,
    _2024_WIREPARK_STAGE_ID,
    _2024_VIDEO_STAGE_ID,
    _2024_DICTANT_STAGE_ID,
    _2024_PORTFOLIO_STAGE_ID);

public static final Long _2024_HACKATHON_QUESTION_ID = 2L;
public static final Long _2024_MATHS_QUESTION_ID = 3L;
public static final Long _2024_WWW_QUESTION_ID = 4L;

private final SeasonRepository seasonRepository;
private final StageRepository stageRepository;

@Override
@Transactional
public void afterSingletonsInstantiated() {
  if (seasonRepository.existsById(_2024_SEASON_ID)) return;

  final List<Season> seasons = seasonRepository.saveAll(List.of(
      Season.builder().title("Студент года 2024").build(),
      Season.builder().title("Суперфинал студент года 2024").build()));

  final Season season = seasons.getFirst();
  final Season finalSeason = seasons.getLast();

  final List<Stage> stages = stageRepository.saveAll(List.of(
      Stage.builder().season(season)
           .stageVisibility(AllowState.YES.name())
           .title("Интеллектуальный этап | Математические бои")
           .description("""
                            Конкурсный этап проводится в формате командного соревнования по решению нестандартных математических задач на время.

                            Две лиги:
                            • лига первокурсников
                            • лига старшекурсников

                            Команды от <b>6 до 10 человек</b>.

                            Дата и время проведения: <b>26 октября</b> 2024 года в <b>16:15</b>.""")
           .build()
           .addFile(null),
      Stage.builder().season(season)
           .stageVisibility(AllowState.YES.name())
           .title("Интеллектуальный этап | Хакатон")
           .description("""
                            25 сентября - 2 октября: Онлайн-отбор
                            19-20 октября: финал в г. Сочи (Сириус)

                            Команды от <b>1 до 3 человек</b>.

                            P.s.: подробная информация появится позже.""")
           .build()
           .addFile(null),
      Stage.builder().season(season)
           .stageVisibility(AllowState.YES.name())
           .title("Интеллектуальный этап | «Что? Где? Когда?»")
           .description("""
                            Конкурсный этап проводится в формате командной викторины с раундами по одной минуте. Участие в Конкурсном этапе допускается только в составе команды:

                            Команды <b>от 3 до 6 человек</b>.

                            Дата и время проведения: <b>24 октября</b> 2023 года в <b>18:00</b>.""")
           .build()
           .addFile(null),

      Stage.builder().season(season)
           .stageVisibility(AllowState.YES.name())
           .title("Спортивный этап | Веревочный парк")
           .description("""
                            Этап пока недоступен :)

                            P.s.: подробная информация появится позже.""")
           .build()
           .addFile(null),
      Stage.builder().season(season)
           .stageVisibility(AllowState.YES.name())
           .title("Спортивный этап | Беговая битва")
           .description("") // todo Наверстать сюда и ниже
           .build()
           .addFile(null),
      Stage.builder().season(season)
           .stageVisibility(AllowState.YES.name())
           .title("Конкурс видео визиток")
           .description("""
                            В рамках этапа необходимо записать видеовизитку с ответом на любые 2 вопроса:
                            Требования к видео:
                            Тайминг: от <b>30 секунд</b> до <b>3 минут</b>
                            Лицо должно составлять <b>не менее 30%</b> от кадра
                            Формат: MP4, MOV, WMV, WEBM, AVI
                            Минимальное разрешение: <b>640х480</b>\s
                            Ориентация: горизонтальная
                            Один дубль без монтажа""")
           .build()
           .addFile(null),

      Stage.builder().season(season)
           .stageVisibility(AllowState.YES.name())
           .title("Патриотический этап | Диктант Победы")
           .description("""
                            Диктант Победы — это исторический диктант на знания о Великой Отечественной Войне.

                            Диктант состоит из\s""")
           .build()
           .addFile(null),
      Stage.builder().season(season)
           .stageVisibility(AllowState.YES.name())
           .title("Проектный этап | Социальные проекты")
           .description("""
                            Этап пока недоступен :)

                            P.s.: подробная информация появится позже.""")
           .build()
           .addFile(null),
      Stage.builder().season(season)
           .stageVisibility(AllowState.YES.name())
           .title("Проектный этап | Научно-исследовательский")
           .description("""
                            Этап пока недоступен :)

                            P.s.: подробная информация появится позже.""")
           .build()
           .addFile(null),
      Stage.builder().season(season)
           .stageVisibility(AllowState.NO.name())
           .title("Конкурс портфолио")
           .description("")
           .build()
           .addFile(null)));
}
}
