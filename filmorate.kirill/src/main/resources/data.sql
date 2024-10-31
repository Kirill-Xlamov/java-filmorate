MERGE INTO public.genres (genre_id, name) VALUES ('1', 'Комедия');
MERGE INTO public.genres (genre_id, name) VALUES ('2', 'Драма');
MERGE INTO public.genres (genre_id, name) VALUES ('3', 'Мультфильм');
MERGE INTO public.genres (genre_id, name) VALUES ('4', 'Триллер');
MERGE INTO public.genres (genre_id, name) VALUES ('5', 'Документальный');
MERGE INTO public.genres (genre_id, name) VALUES ('6', 'Боевик');

MERGE INTO public.mpa (mpa_id, name) VALUES ('1', 'G');
MERGE INTO public.mpa (mpa_id, name) VALUES ('2', 'PG');
MERGE INTO public.mpa (mpa_id, name) VALUES ('3', 'PG-13');
MERGE INTO public.mpa (mpa_id, name) VALUES ('4', 'R');
MERGE INTO public.mpa (mpa_id, name) VALUES ('5', 'NC-17');