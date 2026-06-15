import { createContext, useContext, useMemo, useState } from 'react';

const DemoContext = createContext(null);

const storage = {
  get(key, fallback = '') {
    return window.localStorage.getItem(key) || fallback;
  },
  set(key, value) {
    if (value === null || value === undefined || value === '') {
      window.localStorage.removeItem(key);
      return;
    }
    window.localStorage.setItem(key, value);
  }
};

export function DemoProvider({ children }) {
  const [currentUserId, setCurrentUserIdState] = useState(() => storage.get('currentUserId'));
  const [selectedVideoId, setSelectedVideoIdState] = useState(() => storage.get('selectedVideoId'));
  const [selectedChannelId, setSelectedChannelIdState] = useState(() => storage.get('selectedChannelId'));
  const [notice, setNotice] = useState(null);

  const value = useMemo(() => ({
    currentUserId,
    selectedVideoId,
    selectedChannelId,
    notice,
    setCurrentUserId(userId) {
      storage.set('currentUserId', userId);
      setCurrentUserIdState(userId || '');
    },
    setSelectedVideoId(videoId) {
      storage.set('selectedVideoId', videoId);
      setSelectedVideoIdState(videoId || '');
    },
    setSelectedChannelId(channelId) {
      storage.set('selectedChannelId', channelId);
      setSelectedChannelIdState(channelId || '');
    },
    showNotice(message, type = 'info') {
      setNotice({ message, type });
    },
    clearNotice() {
      setNotice(null);
    }
  }), [currentUserId, notice, selectedChannelId, selectedVideoId]);

  return (
    <DemoContext.Provider value={value}>
      {children}
    </DemoContext.Provider>
  );
}

export function useDemo() {
  const context = useContext(DemoContext);
  if (!context) {
    throw new Error('useDemo must be used inside DemoProvider');
  }
  return context;
}
