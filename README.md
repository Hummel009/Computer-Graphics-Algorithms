[![Code Smells][code_smells_badge]][code_smells_link]
[![Maintainability Rating][maintainability_rating_badge]][maintainability_rating_link]
[![Security Rating][security_rating_badge]][security_rating_link]
[![Bugs][bugs_badge]][bugs_link]
[![Vulnerabilities][vulnerabilities_badge]][vulnerabilities_link]
[![Duplicated Lines (%)][duplicated_lines_density_badge]][duplicated_lines_density_link]
[![Reliability Rating][reliability_rating_badge]][reliability_rating_link]
[![Quality Gate Status][quality_gate_status_badge]][quality_gate_status_link]
[![Technical Debt][technical_debt_badge]][technical_debt_link]
[![Lines of Code][lines_of_code_badge]][lines_of_code_link]

Мои лабораторные работы для BSUIR/БГУИР (белорусский государственный университет информатики и радиоэлектроники).

Предмет - AKG/АКГ (алгоритмы компьютерной графики).

## Условия

Все работы являются программами на тему компьютерной графики. Каждая следующая работа является развитием предыдущей.
Ретроспективно работы были улучшены и пофикшены, но даже в неисправленном виде были успешно сданы.

Суть всех лабораторных: объект, с которым можно взаимодействовать: вращать его при помощи мыши.

### Лабораторная работа 1

* Разработать парсер файлов формата .obj;
* Реализовать матричные преобразования координат из пространства модели в мировое пространство;
* Реализовать матричное преобразование координат из мирового пространства в пространство наблюдателя;
* Реализовать матричное преобразование координат из пространства наблюдателя в пространство проекции;
* Реализовать матричное преобразование координат из пространства проекции в пространство окна просмотра;
* Отрендерить 3D модель в проволочном виде (границы полигонов).

Рисование отдельных линий реализовано по алгоритму Брезенхема.

В репозитории находится два модуля с двумя разными реализациями лабораторной работы: на Kotlin JVM и Kotlin Native.
Второй выдаёт больше FPS.

### Лабораторная работа 2

* Реализовать алгоритм растеризации треугольников;
* Реализовать отбраковку невидимых и задних поверхностей трехмерных объектов;
* Реализовать плоское затенение и модель освещения Ламберта (diffuse);
* Отрендерить 3D модель с поверхностями и освещением.

В репозитории находится два модуля с двумя разными реализациями лабораторной работы: на Kotlin JVM и Kotlin Native.
Второй выдаёт больше FPS.

### Лабораторная работа 3

* Реализовать модель освещения Фонга (ambient + diffuse + specular);
* Отрендерить 3D модель с поверхностями и освещением.

В репозитории находится два модуля с двумя разными реализациями лабораторной работы: на Kotlin JVM и Kotlin Native.
Второй выдаёт больше FPS.

### Лабораторная работа 4

* Реализовать парсер карт mrao, normal, diffuse;
* Применить данные из карт в освещении, чтобы сделать его цветным;
* Отрендерить 3D модель с поверхностями и освещением в цвете.

Разработку на Kotlin Native мы не потянули из-за того, что не получилось прочитать битмапы. Поэтому в репозитории
находится только модуль на Kotlin JVM.

### Лабораторная работа 5

* Перенести графику на GPU.

Была использована сторонняя библиотека, LWJGL. Она используется, например, в игре Minecraft — вся её графика написана
при помощи этой библиотеки. На GPU были перенесены все четыре лабораторные работы (их варианты JVM, так как разработка
Native была слишком трудоёмка, хоть и выдавала более хороший FPS). Лабораторные работы 1-2 были перенесены на GPU
основательно, там были заменены целые функции (рисование линии и рисование полигона). Лабораторные работы 3-4 были
перенесены на более низком уровне — алгоритмы остались свои, была заменена лишь функция рисования пикселя.

Чем проще лаба, чем меньше вычислений и чем примитивнее модель, тем более большой FPS выдаёт GPU.

<!----------------------------------------------------------------------------->

[code_smells_badge]: https://sonarcloud.io/api/project_badges/measure?project=hummel009_Computer-Graphics-Algorithms&metric=code_smells

[code_smells_link]: https://sonarcloud.io/summary/overall?id=hummel009_Computer-Graphics-Algorithms

[maintainability_rating_badge]: https://sonarcloud.io/api/project_badges/measure?project=hummel009_Computer-Graphics-Algorithms&metric=sqale_rating

[maintainability_rating_link]: https://sonarcloud.io/summary/overall?id=hummel009_Computer-Graphics-Algorithms

[security_rating_badge]: https://sonarcloud.io/api/project_badges/measure?project=hummel009_Computer-Graphics-Algorithms&metric=security_rating

[security_rating_link]: https://sonarcloud.io/summary/overall?id=hummel009_Computer-Graphics-Algorithms

[bugs_badge]: https://sonarcloud.io/api/project_badges/measure?project=hummel009_Computer-Graphics-Algorithms&metric=bugs

[bugs_link]: https://sonarcloud.io/summary/overall?id=hummel009_Computer-Graphics-Algorithms

[vulnerabilities_badge]: https://sonarcloud.io/api/project_badges/measure?project=hummel009_Computer-Graphics-Algorithms&metric=vulnerabilities

[vulnerabilities_link]: https://sonarcloud.io/summary/overall?id=hummel009_Computer-Graphics-Algorithms

[duplicated_lines_density_badge]: https://sonarcloud.io/api/project_badges/measure?project=hummel009_Computer-Graphics-Algorithms&metric=duplicated_lines_density

[duplicated_lines_density_link]: https://sonarcloud.io/summary/overall?id=hummel009_Computer-Graphics-Algorithms

[reliability_rating_badge]: https://sonarcloud.io/api/project_badges/measure?project=hummel009_Computer-Graphics-Algorithms&metric=reliability_rating

[reliability_rating_link]: https://sonarcloud.io/summary/overall?id=hummel009_Computer-Graphics-Algorithms

[quality_gate_status_badge]: https://sonarcloud.io/api/project_badges/measure?project=hummel009_Computer-Graphics-Algorithms&metric=alert_status

[quality_gate_status_link]: https://sonarcloud.io/summary/overall?id=hummel009_Computer-Graphics-Algorithms

[technical_debt_badge]: https://sonarcloud.io/api/project_badges/measure?project=hummel009_Computer-Graphics-Algorithms&metric=sqale_index

[technical_debt_link]: https://sonarcloud.io/summary/overall?id=hummel009_Computer-Graphics-Algorithms

[lines_of_code_badge]: https://sonarcloud.io/api/project_badges/measure?project=hummel009_Computer-Graphics-Algorithms&metric=ncloc

[lines_of_code_link]: https://sonarcloud.io/summary/overall?id=hummel009_Computer-Graphics-Algorithms
