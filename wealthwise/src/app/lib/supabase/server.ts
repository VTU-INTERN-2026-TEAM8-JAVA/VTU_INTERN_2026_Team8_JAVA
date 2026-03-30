import { createServerClient } from "@supabase/ssr";
import { cookies } from "next/headers";

export async function createClient() {
  const cookieStore = await cookies();
  return createServerClient(
    process.env.https://exkqbxsefffkclseoxwa.supabase.co!,
    process.env.eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImV4a3FieHNlZmZma2Nsc2VveHdhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzMyNDg2MjQsImV4cCI6MjA4ODgyNDYyNH0.fIwXub1sh1X9648KEDa8W9j5PnbiWbzq0OhnhP0kU7w!,
    {
      cookies: {
        getAll() { return cookieStore.getAll(); },
        setAll(cookiesToSet) {
          try {
            cookiesToSet.forEach(({ name, value, options }) =>
              cookieStore.set(name, value, options)
            );
          } catch {}
        },
      },
    }
  );
}