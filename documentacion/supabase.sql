-- Esquema para una práctica académica con datos ficticios únicamente.
-- Las políticas anónimas exponen los registros a cualquier persona con la URL pública.
-- PRECAUCIÓN: este script elimina cinco tablas públicas heredadas antes de crear personas.
-- Antes de ejecutarlo en otro proyecto, confirma que esas tablas no contienen datos.
-- En el proyecto de esta práctica se verificó que las cinco estaban vacías.
-- Se eliminan solo esas tablas antiguas, en orden de dependencia, sin usar CASCADE.

drop table if exists public.ban_history;
drop table if exists public.last_rides;
drop table if exists public.passengers;
drop table if exists public.drivers;
drop table if exists public.coordenadas;

create table if not exists public.personas (
    id uuid primary key default gen_random_uuid(),
    foto_url text not null,
    nombres text not null,
    primer_apellido text not null,
    segundo_apellido text not null,
    hobbies jsonb not null,
    estado_civil text not null,
    genero text not null,
    grado_estudios text not null,
    edad integer not null check (edad between 0 and 120),
    fecha_nacimiento date not null,
    estacion_preferida text not null,
    latitud double precision not null,
    longitud double precision not null,
    altitud double precision not null,
    localidad text not null,
    fecha_registro timestamptz not null default now(),
    estado boolean not null default true
);

alter table public.personas enable row level security;

drop policy if exists "personas_demo_select_anon" on public.personas;
create policy "personas_demo_select_anon"
    on public.personas for select to anon using (true);

drop policy if exists "personas_demo_insert_anon" on public.personas;
create policy "personas_demo_insert_anon"
    on public.personas for insert to anon with check (true);

-- El bucket es público para que la aplicación muestre la URL de cada fotografía.
insert into storage.buckets (id, name, public)
values ('personas-fotos', 'personas-fotos', true)
on conflict (id) do update set public = excluded.public;

drop policy if exists "personas_fotos_demo_upload_anon" on storage.objects;
create policy "personas_fotos_demo_upload_anon"
    on storage.objects for insert to anon
    with check (bucket_id = 'personas-fotos');

drop policy if exists "personas_fotos_demo_delete_anon" on storage.objects;
create policy "personas_fotos_demo_delete_anon"
    on storage.objects for delete to anon
    using (bucket_id = 'personas-fotos');
