begin 
  select 1 -- Noncompliant {{Handle exceptions of this query.}}
    into var
    from dual;
end;
/

begin
  if (true) then 
    select 1 -- Noncompliant
      into var
      from dual;
  end if;
end;
/

begin

    begin
      select 1 -- Noncompliant, inner block without exception handling
        into var
        from dual;
    end;

exception
  when others then
    null;
end;
/

create procedure foo is
begin
  select 1 -- Noncompliant
    into var
    from dual;
end;
/

create function foo return number is
begin
  select 1 -- Noncompliant
    into var
    from dual;
end;
/

create package body pack is
    procedure foo is
    begin
      select 1 -- Noncompliant
        into var
        from dual;
    end;
    
    function foo return number is
    begin
      select 1 -- Noncompliant
        into var
        from dual;
    end;
end;
/

-- correct code
begin 
  select 1
    into var
    from dual;
exception
  when others then
    null;
end;
/

begin -- outer block doesn't require an exception handling block
    begin 
      select 1
        into var
        from dual;
    exception
      when others then
        null;
    end;
end;
/

create trigger foo
before insert on tab
begin
  select 1
    into var
    from dual;
exception
  when others then
    null;
end;
/

-- queries with bulk collect should be ignored because they don't throw no_data_found/too_many_rows
begin
  select 1
    bulk collect into var
    from dual;
end;
/
