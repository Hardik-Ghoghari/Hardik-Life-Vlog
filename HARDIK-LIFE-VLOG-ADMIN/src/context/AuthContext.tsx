import React, { createContext, useContext, useState, useEffect } from 'react';
import { UserAccount } from '../types';

interface AuthContextType {
  currentUser: UserAccount | null;
  isAdmin: boolean;
  isLoading: boolean;
  login: (email: string, pass: string) => Promise<{ success: boolean; error?: string; role?: string }>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [currentUser, setCurrentUser] = useState<UserAccount | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    // Check saved session in local storage
    const saved = localStorage.getItem('hlv_admin_session');
    if (saved) {
      try {
        const user = JSON.parse(saved);
        setCurrentUser(user);
      } catch {
        localStorage.removeItem('hlv_admin_session');
      }
    }
    setIsLoading(false);
  }, []);

  const login = async (email: string, pass: string): Promise<{ success: boolean; error?: string; role?: string }> => {
    setIsLoading(true);
    // Emulate authentication & secure role check
    await new Promise(r => setTimeout(r, 600));

    if (!email || !pass) {
      setIsLoading(false);
      return { success: false, error: 'Please provide both email and password' };
    }

    // Check admin credentials
    // Default admin: hardik@lifevlog.com or admin@hardikvlog.com
    const cleanEmail = email.trim().toLowerCase();
    
    // Normal viewer attempt test case
    if (cleanEmail === 'viewer@gmail.com' || cleanEmail === 'user@example.com') {
      const viewerUser: UserAccount = {
        id: 'usr_viewer_99',
        name: 'Regular Viewer',
        email: cleanEmail,
        role: 'user',
        status: 'active',
        createdAt: Date.now()
      };
      setIsLoading(false);
      return { 
        success: false, 
        error: 'Access Denied: Only users with role = "admin" can access the Hardik Life Vlog Admin Panel.',
        role: 'user' 
      };
    }

    // Valid admin login
    if (cleanEmail.includes('hardik') || cleanEmail.includes('admin') || pass === 'admin123' || pass === 'hardik@2026') {
      const adminUser: UserAccount = {
        id: 'usr_admin',
        name: 'Hardik Patel',
        email: cleanEmail,
        role: 'admin',
        status: 'active',
        createdAt: Date.now() - 31536000000,
        lastLogin: Date.now()
      };
      setCurrentUser(adminUser);
      localStorage.setItem('hlv_admin_session', JSON.stringify(adminUser));
      setIsLoading(false);
      return { success: true, role: 'admin' };
    }

    setIsLoading(false);
    return { success: false, error: 'Invalid credentials. Please use an authorized administrator account.' };
  };

  const logout = () => {
    setCurrentUser(null);
    localStorage.removeItem('hlv_admin_session');
  };

  return (
    <AuthContext.Provider
      value={{
        currentUser,
        isAdmin: currentUser?.role === 'admin',
        isLoading,
        login,
        logout
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
